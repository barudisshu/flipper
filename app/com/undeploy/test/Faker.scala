package com.undeploy.test

import org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.apache.commons.lang3.RandomStringUtils.random
import java.util.UUID
import java.util.Locale
import org.joda.time.DateTime
import com.undeploy.flipper.User
import scalaoauth2.provider.AuthInfo
import com.undeploy.oauth2.AuthCode
import scala.util.Random
import org.apache.commons.lang3.RandomStringUtils
import com.undeploy.flipper.Password
import com.undeploy.flipper.OAuth2Client

object Faker {

  def url() =
    s"http://${randomAlphanumeric(10)}.com/${randomAlphanumeric(10)}"

  def email() =
    s"${randomAlphanumeric(10)}@yeolab.com".toLowerCase

  def oauth2Scope() = Seq.fill(2)(randomAlphabetic(5)).mkString(",")

  def alphaNumeric(n: Int = 10) = randomAlphanumeric(n)

  def randomString(n: Int = 10) = random(n)

  def user() = User(
    Faker.email(),
    UUID.randomUUID(),
    Locale.ITALY,
    DateTime.now(),
    DateTime.now())

  def authInfo() = AuthInfo[User](
    user(),
    Some(alphaNumeric()),
    Some(oauth2Scope()),
    Some(url()))

  def authCode() = AuthCode(
    alphaNumeric(),
    UUID.randomUUID(),
    Some(url()),
    DateTime.now(),
    Some(oauth2Scope()),
    Some(alphaNumeric()),
    new Random().nextLong())

  def password() = Password(
    email(),
    randomString())

  def oauth2Client() = OAuth2Client(
    alphaNumeric(),
    Some(alphaNumeric()),
    UUID.randomUUID(),
    Some(url()),
    Some(oauth2Scope()),
    Set("client_credentials"))

}