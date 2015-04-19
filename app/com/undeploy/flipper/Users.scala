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

case class PUser(
  email: String,
  id: UUID,
  locale: String,
  createdAt: Date,
  lastUpdate: Date)

trait PUsers {
  def findByEmail(email: String): Future[Option[PUser]]
  def findById(id: UUID): Future[Option[PUser]]
  def insert(user: PUser): Future[PUser]
  def update(user: PUser): Future[PUser]
  def deleteByEmail(email: String): Future[String]
}

case class User(
  email: String,
  id: UUID,
  locale: Locale,
  createdAt: DateTime,
  lastUpdate: DateTime)

class Users(pUsers: PUsers) {

  implicit def toPUser(user: User): PUser = {
    Option(user) map { u =>
      PUser(
        user.email.toLowerCase,
        user.id,
        user.locale,
        user.createdAt,
        user.lastUpdate)
    } orNull
  }

  implicit def fromPUser(user: PUser): User = {
    Option(user) map { u =>
      User(
        user.email,
        user.id,
        user.locale,
        user.createdAt,
        user.lastUpdate)
    } orNull
  }

  implicit def fromPUser(user: Option[PUser]): Option[User] = {
    user.map { x => x }
  }

  def findByEmail(email: String): Future[Option[User]] = {
    pUsers.findByEmail(email).map { u => u }
  }

  def findById(id: UUID): Future[Option[User]] = {
    pUsers.findById(id).map { u => u }
  }

  def save(user: User): Future[User] = {
    val now = DateTime.now()
    findByEmail(user.email).flatMap(
      _.map({ u =>
        pUsers.update(u.copy(locale = user.locale, lastUpdate = now))
      }).getOrElse({
        pUsers.insert(user.copy(id = UUID.randomUUID(), createdAt = now, lastUpdate = now))
      })).map({ puser => puser })
  }

  def deleteByEmail(email: String): Future[String] = {
    pUsers.deleteByEmail(email)
  }

}

object Users {
  def apply(pUsers: PUsers) = new Users(pUsers)
}

