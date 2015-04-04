package com.undeploy.test

import org.apache.commons.lang3.RandomStringUtils
import java.util.UUID
import java.util.Locale
import org.joda.time.DateTime
import com.undeploy.flipper.User

object Faker {
  def email() =
    s"${RandomStringUtils.randomAlphanumeric(10)}@yeolab.com"

  def user() = User(
    Faker.email(),
    UUID.randomUUID(),
    Locale.ITALY,
    DateTime.now(),
    DateTime.now())

}