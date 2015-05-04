package com.undeploy

import org.scalatest.FunSuite
import scala.concurrent._
import ExecutionContext.Implicits.global
import com.undeploy.test.TestApplicationContext._
import com.undeploy.oauth2.CassandraOAuth2DataHandler
import com.undeploy.test.TestApplicationContext
import scalaoauth2.provider.AuthInfo
import com.undeploy.flipper.User
import com.undeploy.test.Faker
import scala.concurrent.Await
import scala.concurrent.duration._
import scalaoauth2.provider.ClientCredential
import scala.concurrent.Await

class CassandraOAuth2DataHandlerSuite extends FunSuite {

  val handler = new CassandraOAuth2DataHandler(
    TestApplicationContext.users,
    TestApplicationContext.passwords,
    TestApplicationContext.clients,
    TestApplicationContext.cassandra)

  test("access token is created") {
    val authInfo = Faker.authInfo()

    val saved = Await.result(
      handler.createAccessToken(authInfo), 1 seconds)

    assert(saved.token != null)
    assert(saved.refreshToken != null)
    assertResult(saved.scope)(authInfo.scope)
    assertResult(saved.expiresIn)(Some(60L * 60L))
    assert(saved.createdAt != null)

    val found = Await.result(
      handler.findAccessToken(saved.token), 1 seconds)

    assertResult(saved)(found.get)
  }

  test("given an access token an auth info is returned") {
    val authInfo = Faker.authInfo()
    val token = Await.result(
      users.save(authInfo.user).flatMap { u =>
        handler.createAccessToken(authInfo.copy(user = u))
      }, 1 seconds)

    val found = Await.result(
      handler.findAuthInfoByAccessToken(token), 1 seconds)

    assertResult(authInfo.user.email)(found.get.user.email)
    assertResult(authInfo.clientId)(found.get.clientId)
    assertResult(authInfo.scope)(found.get.scope)
  }

  test("given an auth code an auth info is returned") {
    val userSaved = Await.result(
      users.save(Faker.user()), 1 seconds)
    val authCodeSaved = Await.result(
      handler.createAuthCode(
        Faker.authCode().copy(userId = userSaved.id)), 1 seconds)

    val authInfo = Await.result(
      handler.findAuthInfoByCode(authCodeSaved.authorizationCode), 1 seconds).get

    assertResult(userSaved)(authInfo.user)
    assertResult(authCodeSaved.clientId)(authInfo.clientId)
    assertResult(authCodeSaved.redirectUri)(authInfo.redirectUri)
    assertResult(authCodeSaved.scope)(authInfo.scope)
  }

  test("given an refresh token an auth info is returned") {
    val authInfo = Faker.authInfo()
    val token = Await.result(
      users.save(authInfo.user).flatMap { u =>
        handler.createAccessToken(authInfo.copy(user = u))
      }, 1 seconds)

    val found = Await.result(
      handler.findAuthInfoByRefreshToken(token.refreshToken.get),
      1 seconds)

    assertResult(authInfo.user.email)(found.get.user.email)
    assertResult(authInfo.clientId)(found.get.clientId)
    assertResult(authInfo.scope)(found.get.scope)
  }

  test("an existing user is found by username and password") {
    val savedUser = Await.result(
      users.save(Faker.user()), 1 seconds)
    val password = Faker.password().copy(email = savedUser.email)
    val savedPassword = Await.result(
      passwords.save(password), 1 seconds)

    val user = Await.result(
      handler.findUser(
        password.email,
        password.password), 1 seconds)

    assertResult(savedUser.email)(user.get.email)
  }

  test("a non-existing user is not found by username and password") {
    val password = Faker.password()

    val user = Await.result(
      handler.findUser(
        password.email,
        password.password), 1 seconds)

    assertResult(None)(user)
  }

  test("when password does not match, user is not found") {
    val password = Faker.password()
    val savedPassword = Await.result(
      passwords.save(password), 1 seconds)

    val user = Await.result(
      handler.findUser(
        password.email,
        Faker.randomString()), 1 seconds)

    assertResult(None)(user)
  }

  test("access token is retrieved by user id and client id") {
    val authInfo = Faker.authInfo()
    val saved = Await.result(
      handler.createAccessToken(authInfo), 1 seconds)

    val found = Await.result(
      handler.getStoredAccessToken(authInfo), 1 seconds)

    assertResult(saved)(found.get)
  }

  test("user is found by client") {
    val user = Await.result(
      users.save(Faker.user()), 1 seconds)
    val client = Await.result(
      clients.save(Faker.oauth2Client().copy(userId = Some(user.id))), 1 seconds)

    val found = Await.result(
      handler.findClientUser(
        ClientCredential(client.id, client.secret), client.scope), 1 seconds)

    assertResult(user)(found.get)
  }

  test("client credential are validated") {
    val client = Await.result(
      clients.save(Faker.oauth2Client().copy()), 1 seconds)

    var valid = Await.result(
      handler.validateClient(
        ClientCredential(client.id, client.secret), client.grantTypes.last), 1 seconds)

    assert(valid)
    
    valid = Await.result(
      handler.validateClient(
        ClientCredential(client.id, Some(Faker.randomString())), client.grantTypes.last), 1 seconds)

    assert(!valid)
    
    valid = Await.result(
      handler.validateClient(
        ClientCredential(Faker.randomString(), client.secret), client.grantTypes.last), 1 seconds)

    assert(!valid)
  }

}