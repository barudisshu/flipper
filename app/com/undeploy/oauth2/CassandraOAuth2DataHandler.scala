package com.undeploy.oauth2

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.mindrot.jbcrypt.BCrypt
import com.datastax.driver.core.Row
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.undeploy.cassandra.Cassandra
import com.undeploy.flipper.Passwords
import com.undeploy.flipper.User
import com.undeploy.flipper.Users
import com.undeploy.lang.Converters.ToDate
import com.undeploy.lang.Converters.ToDateTime
import scalaoauth2.provider.AccessToken
import scalaoauth2.provider.AuthInfo
import scalaoauth2.provider.ClientCredential
import scalaoauth2.provider.DataHandler
import com.undeploy.flipper.OAuth2Clients
import com.undeploy.flipper.OAuth2Client

case class UserAccessToken(
  accessToken: String,
  refreshToken: Option[String],
  userId: UUID,
  scope: Option[String],
  expiresIn: Option[Long],
  createdAt: DateTime,
  clientId: Option[String])

case class AuthCode(
  authorizationCode: String,
  userId: UUID,
  redirectUri: Option[String],
  createdAt: DateTime,
  scope: Option[String],
  clientId: Option[String],
  expiresIn: Long)

class CassandraOAuth2DataHandler(
  users: Users,
  passwords: Passwords,
  clients: OAuth2Clients,
  cassandra: Cassandra) extends DataHandler[User] {

  implicit def toAccessToken(accessToken: UserAccessToken): AccessToken = {
    scalaoauth2.provider.AccessToken(
      accessToken.accessToken,
      accessToken.refreshToken,
      accessToken.scope,
      accessToken.expiresIn,
      accessToken.createdAt)
  }

  implicit def toAccessToken(accessToken: Option[UserAccessToken]): Option[AccessToken] = {
    accessToken.map { t => t }
  }

  implicit def toAccessToken(row: Row): Option[UserAccessToken] = {
    Option(row) map { r =>
      UserAccessToken(
        r.getString("access_token"),
        Some(r.getString("refresh_token")),
        r.getUUID("user_id"),
        Some(r.getString("scope")),
        Some(r.getLong("expires_id")),
        r.getDate("created_at"),
        Some(r.getString("client_id")))
    }
  }

  implicit def toAuthCode(row: Row): Option[AuthCode] = {
    Option(row) map { r =>
      AuthCode(
        r.getString("authorization_code"),
        r.getUUID("user_id"),
        Some(r.getString("redirect_uri")),
        r.getDate("created_at"),
        Some(r.getString("scope")),
        Some(r.getString("client_id")),
        r.getLong("expires_in"))
    }
  }

  override def createAccessToken(authInfo: AuthInfo[User]): Future[AccessToken] = {
    val accessToken = UserAccessToken(
      Crypto.generateToken,
      Some(Crypto.generateToken),
      authInfo.user.id,
      authInfo.scope,
      Some(60L * 60L),
      DateTime.now(DateTimeZone.UTC),
      authInfo.clientId)

    val q = QueryBuilder.update("accesstokens")
      .`with`(
        QueryBuilder.set("access_token", accessToken.accessToken))
      .and(QueryBuilder.set("created_at", ToDate(accessToken.createdAt)))

    accessToken.refreshToken.foreach({ v => q.and(QueryBuilder.set("refresh_token", v)) })
    accessToken.scope.foreach({ v => q.and(QueryBuilder.set("scope", v)) })
    accessToken.expiresIn.foreach({ v =>
      q.and(QueryBuilder.set("expires_id", v))
      q.using(QueryBuilder.ttl(accessToken.expiresIn.get.toInt))
    })

    cassandra.executeAsync(
      q.where(QueryBuilder.eq("user_id", accessToken.userId))
        .and(QueryBuilder.eq("client_id", accessToken.clientId.get)))
      .map { res => accessToken }
  }

  def findUserAccessToken(token: String): Future[Option[UserAccessToken]] = {
    cassandra.executeAsync(QueryBuilder.select()
      .all().from("accesstokens")
      .where(QueryBuilder.eq("access_token", token)))
      .map { res => res.one() }
  }

  def findUserAccessToken(userId: UUID, clientId: String): Future[Option[UserAccessToken]] = {
    cassandra.executeAsync(QueryBuilder.select()
      .all().from("accesstokens")
      .where(QueryBuilder.eq("user_id", userId))
      .and(QueryBuilder.eq("client_id", clientId)))
      .map { res => res.one() }
  }

  def findUserAccessTokenByRefreshToken(refreshToken: String): Future[Option[UserAccessToken]] = {
    cassandra.executeAsync(QueryBuilder.select()
      .all().from("accesstokens")
      .where(QueryBuilder.eq("refresh_token", refreshToken)))
      .map { res => res.one() }
  }

  override def findAccessToken(token: String): Future[Option[AccessToken]] = {
    findUserAccessToken(token)
      .map(res => res)
  }

  override def findAuthInfoByAccessToken(accessToken: AccessToken): Future[Option[AuthInfo[User]]] = {
    findUserAccessToken(accessToken.token).flatMap { optToken =>
      optToken.map { token =>
        users.findById(token.userId).map {
          case Some(user) =>
            Some(AuthInfo(user, token.clientId, token.scope, None))
          case _ => None
        }
      }.getOrElse(Future.successful(None))
    }
  }

  def createAuthCode(authCode: AuthCode): Future[AuthCode] = {
    val q = QueryBuilder.update("authcodes")
      .`with`(
        QueryBuilder.set("user_id", authCode.userId))
      .and(QueryBuilder.set("created_at", ToDate(authCode.createdAt)))
      .and(QueryBuilder.set("expires_in", authCode.expiresIn.toInt))

    authCode.clientId.foreach(v => q.and(QueryBuilder.set("client_id", v)))
    authCode.scope.foreach(v => q.and(QueryBuilder.set("scope", v)))
    authCode.redirectUri.foreach({ v => q.and(QueryBuilder.set("redirect_uri", v)) })

    cassandra.executeAsync(
      q.where(QueryBuilder.eq("authorization_code", authCode.authorizationCode)))
      .map(res => authCode)
  }

  def findAuthCode(code: String): Future[Option[AuthCode]] = {
    cassandra.executeAsync(QueryBuilder.select()
      .all().from("authcodes")
      .where(QueryBuilder.eq("authorization_code", code)))
      .map(res => res.one())
  }

  override def findAuthInfoByCode(code: String): Future[Option[AuthInfo[User]]] = {
    findAuthCode(code).flatMap { optCode =>
      optCode.map { token =>
        users.findById(token.userId).map {
          case Some(user) =>
            Some(AuthInfo(user, token.clientId, token.scope, token.redirectUri))
          case _ => None
        }
      }.getOrElse(Future.successful(None))
    }
  }

  override def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[User]]] = {
    findUserAccessTokenByRefreshToken(refreshToken).flatMap { optToken =>
      optToken.map { token =>
        users.findById(token.userId).map {
          case Some(user) =>
            Some(AuthInfo(user, token.clientId, token.scope, None))
          case _ => None
        }
      }.getOrElse(Future.successful(None))
    }
  }

  override def findClientUser(clientCredential: ClientCredential, scope: Option[String]): Future[Option[User]] = {
    val s = scope.getOrElse("default")
    clients.find(clientCredential.clientId, clientCredential.clientSecret)
      .flatMap {
        case Some(c) => if (c.scope.contains(s)) users.findById(c.userId) else Future.successful(None)
        case None    => Future.successful(None)
      }
  }

  override def findUser(username: String, password: String): Future[Option[User]] = {
    passwords.find(username).flatMap {
      case Some(p) =>
        if (BCrypt.checkpw(password, p.password)) users.findByEmail(username)
        else Future.successful(None)
      case _ => Future.successful(None)
    }
  }

  override def getStoredAccessToken(authInfo: AuthInfo[User]): Future[Option[AccessToken]] = {
    authInfo.clientId.map({ clientId =>
      findUserAccessToken(authInfo.user.id, clientId).map(toAccessToken(_))
    }).getOrElse(Future.successful(None))
  }

  override def refreshAccessToken(authInfo: AuthInfo[User], refreshToken: String): Future[AccessToken] = {
    createAccessToken(authInfo)
  }

  override def validateClient(clientCredential: ClientCredential, grantType: String): Future[Boolean] = {
    clients.find(clientCredential.clientId, clientCredential.clientSecret).map {
      case Some(c) => c.grantTypes.contains(grantType)
      case _       => false
    }
  }
}

object Crypto {
  def generateToken: String = {
    val key = java.util.UUID.randomUUID.toString
    new sun.misc.BASE64Encoder().encode(key.getBytes)
  }
}