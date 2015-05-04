package com.undeploy.flipper.dao

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import com.datastax.driver.core.Row
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.undeploy.cassandra.Cassandra
import com.undeploy.lang.Converters._
import org.mindrot.jbcrypt.BCrypt
import com.undeploy.flipper.PPasswords
import com.undeploy.flipper.PPassword

class CassandraPasswords(cassandra: Cassandra) extends PPasswords {

  implicit def rowToUser(row: Row): Option[PPassword] = {
    Option(row) map { r =>
      PPassword(
        r.getString("email"),
        r.getString("password"))
    }
  }

  override def find(email: String): Future[Option[PPassword]] = {
    cassandra
      .executeAsync(QueryBuilder.select()
        .all().from("passwords")
        .where(QueryBuilder.eq("email", email.toLowerCase)))
      .map(res => res.one())
  }

  override def save(password: PPassword): Future[PPassword] = {
    cassandra
      .executeAsync(QueryBuilder
        .update("passwords")
        .`with`(QueryBuilder.set("password", password.password))
        .where(QueryBuilder.eq("email", password.email)))
      .map(res => password)
  }
}