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

class OAuth2ClientsSuite extends FunSuite {

  test("client is saved") {
    val saved = Await.result(
      clients.save(Faker.oauth2Client()), 1 seconds)

    val found = Await.result(
      clients.find(saved.id, saved.secret), 1 seconds)

    assertResult(saved)(found.get)
  }

}