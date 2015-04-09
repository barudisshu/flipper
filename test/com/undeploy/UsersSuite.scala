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

class UsersSuite extends FunSuite {
  test("when user is not found, none is returned") {
    val user = Await.result(
      users.findByEmail(Faker.email()), 1 seconds)

    assertResult(None)(user)
  }

  test("user is saved") {
    val user = Faker.user()

    val saved = Await.result(
      users.save(user), 1 seconds)

    val found = Await.result(
      users.findByEmail(user.email), 1 seconds)

    assertResult(saved)(found.get)
  }

  test("user is deleted") {
    val user = Faker.user()

    val saved = Await.result(
      users.save(user), 1 seconds)

    val deleted = Await.result(
      users.deleteByEmail(user.email), 1 seconds)

    val found = Await.result(
      users.findByEmail(user.email), 1 seconds)

    assertResult(user.email)(deleted)
    assertResult(None)(found)
  }
}