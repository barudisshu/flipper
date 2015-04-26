package com.undeploy.flipper.dao

import scala.concurrent._
import ExecutionContext.Implicits.global
import com.undeploy.cassandra.Cassandra
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.Row
import com.undeploy.lang.Converters._
import java.util.UUID
import com.undeploy.flipper.PUser
import com.undeploy.flipper.PUsers

class CassandraUsers(cassandra: Cassandra) extends PUsers {

  implicit def rowToUser(row: Row): Option[PUser] = {
    Option(row) map { r =>
      PUser(
        r.getString("email"),
        r.getUUID("id"),
        r.getString("locale"),
        r.getDate("created_at"),
        r.getDate("last_update"))
    }
  }

  override def findByEmail(email: String): Future[Option[PUser]] = {
    cassandra
      .executeAsync(QueryBuilder.select()
        .all().from("users")
        .where(QueryBuilder.eq("email", email.toLowerCase)))
      .map(res => res.one())
  }

  override def findById(id: UUID): Future[Option[PUser]] = {
    cassandra
      .executeAsync(QueryBuilder.select()
        .all().from("users")
        .where(QueryBuilder.eq("id", id)))
      .map(res => res.one())
  }

  override def insert(user: PUser): Future[PUser] = {
    cassandra
      .executeAsync(QueryBuilder
        .insertInto("users")
        .value("email", user.email)
        .value("id", user.id)
        .value("locale", user.locale)
        .value("created_at", user.createdAt)
        .value("last_update", user.lastUpdate))
      .map(res => user)
  }

  override def update(user: PUser): Future[PUser] = {
    cassandra
      .executeAsync(QueryBuilder
        .update("users")
        .`with`(QueryBuilder.set("locale", user.locale))
        .and(QueryBuilder.set("last_update", user.lastUpdate))
        .where(QueryBuilder.eq("email", user.email)))
      .map(res => user)
  }

  override def deleteByEmail(email: String): Future[String] = {
    cassandra
      .executeAsync(QueryBuilder.delete()
        .from("users")
        .where(QueryBuilder.eq("email", email.toLowerCase)))
      .map(res => email)
  }
}