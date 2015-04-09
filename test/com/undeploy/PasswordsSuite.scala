package com.undeploy

import org.scalatest.FunSuite

import scala.util.{ Success, Failure }
import scala.concurrent._
import ExecutionContext.Implicits.global
import com.undeploy.test.TestApplicationContext._
import com.undeploy.test.Faker
import scala.concurrent.duration._
import com.undeploy.flipper.User
import com.ibm.icu.util.ULocale
import org.joda.time.DateTime
import java.util.Locale
import java.util.UUID
import com.datastax.driver.core.querybuilder.Insert
import com.datastax.driver.core.querybuilder.QueryBuilder

class PasswordSuite extends FunSuite {

  test("when the user is not found, none password is returned") {
    val password = Await.result(
      passwords.find(Faker.email()), 1 seconds)

    assertResult(None)(password)
  }

  test("passowrd is saved") {
    val saved = Await.result(
      passwords.save(Faker.password()), 1 seconds)

    val found = Await.result(
      passwords.find(saved.email), 1 seconds)

    assertResult(saved)(found.get)
  }

}