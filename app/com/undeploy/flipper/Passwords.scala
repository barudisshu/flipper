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

case class PPassword(
  email: String,
  password: String)

trait PPasswords {
  def find(email: String): Future[Option[PPassword]]
  def save(password: PPassword): Future[PPassword]
}

case class Password(
  email: String,
  password: String)

class Passwords(pPasswords: PPasswords) {

  implicit def toPPassword(password: Password): PPassword = {
    Option(password).map(p => PPassword(p.email.toLowerCase, p.password)).orNull
  }

  implicit def fromPPassword(password: PPassword): Password = {
    Option(password).map(p => Password(p.email, p.password)).orNull
  }

  implicit def fromPPassword(user: Option[PPassword]): Option[Password] = {
    user.map(x => x)
  }

  def find(email: String): Future[Option[Password]] = {
    pPasswords.find(email).map(x => x)
  }

  def save(password: Password): Future[Password] = {
    val salt = BCrypt.gensalt(10)
    val hash = BCrypt.hashpw(password.password, salt)
    val hashPassword = password.copy(password = hash)
    pPasswords.save(hashPassword).map(x => x)
  }

}

object Passwords {
  def apply(pPasswords: PPasswords) = new Passwords(pPasswords)
}

