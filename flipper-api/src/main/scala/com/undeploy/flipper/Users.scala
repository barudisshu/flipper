package com.undeploy.flipper

import java.util.Date
import java.util.Locale
import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import org.joda.time.DateTime

import com.undeploy.lang.Converters.ToLocale
import com.undeploy.lang.Converters.ToString
import com.undeploy.lang.Time

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
        Time.toDate(user.createdAt),
        Time.toDate(user.lastUpdate))
    } orNull
  }

  implicit def fromPUser(user: PUser): User = {
    Option(user) map { u =>
      User(
        user.email,
        user.id,
        user.locale,
        Time.toDateTime(user.createdAt),
        Time.toDateTime(user.lastUpdate))
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
    val now = Time.timestamp()
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

