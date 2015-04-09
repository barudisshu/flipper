package com.undeploy.flipper

import com.undeploy.cassandra.Cassandra

import scala.concurrent._
import ExecutionContext.Implicits.global
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Row
import java.util.UUID
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.util.Date
import com.ibm.icu.util.ULocale
import java.util.Locale
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.querybuilder.QueryBuilder._
import com.undeploy.lang.Converters._
import org.mindrot.jbcrypt.BCrypt

case class POAuth2Client(
  id: String,
  secret: Option[String],
  userId: UUID,
  redirectUri: Option[String],
  scope: Option[String],
  grantTypes: Set[String])

trait POAuth2Clients {
  def find(id: String, secret: Option[String] = None): Future[Option[POAuth2Client]]
  def save(client: POAuth2Client): Future[POAuth2Client]
}

case class OAuth2Client(
  id: String,
  secret: Option[String],
  userId: UUID,
  redirectUri: Option[String],
  scope: Option[String],
  grantTypes: Set[String])

class OAuth2Clients(pClients: POAuth2Clients) {

  implicit def toPClient(client: OAuth2Client): POAuth2Client = {
    Option(client).map(p => POAuth2Client(
      p.id,
      p.secret,
      p.userId,
      p.redirectUri,
      p.scope,
      p.grantTypes)).orNull
  }

  implicit def fromPClient(client: POAuth2Client): OAuth2Client = {
    Option(client).map(p => OAuth2Client(
      p.id,
      p.secret,
      p.userId,
      p.redirectUri,
      p.scope,
      p.grantTypes)).orNull
  }

  implicit def fromPClient(client: Option[POAuth2Client]): Option[OAuth2Client] = {
    client.map(x => x)
  }

  def find(id: String, secret: Option[String]): Future[Option[OAuth2Client]] = {
    pClients.find(id, secret) map (x => x)
  }

  def save(client: OAuth2Client): Future[OAuth2Client] = {
    pClients.save(client).map(x => x)
  }

}

object OAuth2Clients {
  def apply(pClients: POAuth2Clients) = new OAuth2Clients(pClients)
}

