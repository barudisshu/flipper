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
import org.joda.time.DateTimeZone
import com.undeploy.lang.Time

object Faker {

  val _random = new Random()

  def uuid() = UUID.randomUUID()

  def timestamp() = Time.timestamp()

  def url() =
    s"http://${randomAlphanumeric(10)}.com/${randomAlphanumeric(10)}"

  def email() =
    s"${randomAlphanumeric(10)}@yeolab.com".toLowerCase

  def oauth2Scope() = Seq.fill(2)(randomAlphabetic(5)).mkString(",")

  def alphaNumeric(n: Int = 10) = randomAlphanumeric(n)

  def randomString(n: Int = 10) = random(n)

  def user() = User(
    Faker.email(),
    uuid(),
    Locale.ITALY,
    timestamp(),
    timestamp())

  def authInfo() = AuthInfo[User](
    user(),
    Some(alphaNumeric()),
    Some(oauth2Scope()),
    Some(url()))

  def authCode() = AuthCode(
    alphaNumeric(),
    uuid(),
    Some(url()),
    timestamp(),
    Some(oauth2Scope()),
    Some(alphaNumeric()),
    _random.nextLong())

  def password() = Password(
    email(),
    randomString())

  def oauth2Client() = OAuth2Client(
    alphaNumeric(),
    Some(alphaNumeric()),
    uuid(),
    Some(url()),
    Some(oauth2Scope()),
    Set("client_credentials"))

}