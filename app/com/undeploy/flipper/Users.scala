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

case class User(
  email: String,
  id: UUID,
  locale: Locale,
  createdAt: DateTime,
  lastLogin: DateTime)

case class PUser(
  email: String,
  id: UUID,
  locale: String,
  createdAt: Date,
  lastLogin: Date)

class Users(cassandra: Cassandra) {

  implicit def userToPUser(user: User): PUser = {
    Option(user) map { u =>
      PUser(
        user.email.toLowerCase,
        user.id,
        user.locale,
        user.createdAt,
        user.lastLogin)
    } orNull
  }

  implicit def puserToUser(user: PUser): User = {
    Option(user) map { u =>
      User(
        user.email,
        user.id,
        user.locale,
        user.createdAt,
        user.lastLogin)
    } orNull
  }

  implicit def dateToDateTime(date: Date): DateTime = {
    Option(date) map { d => new DateTime(d.getTime, DateTimeZone.UTC) } orNull
  }

  implicit def dateTimeToDate(dateTime: DateTime): Date = {
    Option(dateTime) map { d => d.toDate() } orNull
  }

  implicit def localeToString(locale: Locale): String = {
    Option(locale) map { loc => loc.toString() } orNull
  }

  implicit def stringToLocale(locale: String): Locale = {
    Option(locale) map { loc => new ULocale(locale).toLocale() } orNull
  }

  implicit def rowToUser(row: Row): Option[PUser] = {
    Option(row) map { r =>
      PUser(
        r.getString("email"),
        r.getUUID("id"),
        r.getString("locale"),
        r.getDate("created_at"),
        r.getDate("last_login"))
    }
  }

  def findByEmail(email: String): Future[Option[User]] = {
    Option(email) map { _email =>
      cassandra
        .executeAsync("select * from users where email = ?", _email.toLowerCase)
        .map { res =>
          rowToUser(res.one())
            .map[User] { p => p }
        }
    } getOrElse (Future.failed(new IllegalArgumentException("Email is null")))
  }

  def save(user: User): Future[User] = {
    val now = DateTime.now()
    findByEmail(user.email).flatMap(
      _.map({ u =>
        update(u.copy(locale = user.locale, lastLogin = now))
      }).getOrElse({   
        insert(user.copy(id = UUID.randomUUID(), createdAt = now, lastLogin = now))
      })).map({ puser => puser })
  }

  def deleteByEmail(email: String): Future[String] = {
    cassandra.executeAsync(QueryBuilder.delete()
      .from("users")
      .where(QueryBuilder.eq("email", email.toLowerCase)))
      .map { res => email }
  }

  def insert(user: PUser): Future[PUser] = {
    cassandra.executeAsync(QueryBuilder
      .insertInto("users")
      .value("email", user.email)
      .value("id", user.id)
      .value("locale", user.locale)
      .value("created_at", user.createdAt)
      .value("last_login", user.lastLogin))
      .map { res => user }
  }

  def update(user: PUser): Future[PUser] = {
    cassandra.executeAsync(QueryBuilder
      .update("users")
      .`with`(set("locale", user.locale))
      .and(set("last_login", user.lastLogin))
      .where(QueryBuilder.eq("email", user.email)))
      .map { res => user }
  }

}

object Users {
  def apply(cassandra: Cassandra) = new Users(cassandra)
}

