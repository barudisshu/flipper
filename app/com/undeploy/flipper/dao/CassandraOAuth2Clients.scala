package com.undeploy.flipper.dao

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import com.datastax.driver.core.Row
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.undeploy.cassandra.Cassandra
import com.undeploy.lang.Converters._
import scala.collection.JavaConverters._
import com.undeploy.flipper.POAuth2Client
import com.undeploy.flipper.POAuth2Clients

class CassandraOAuth2Clients(cassandra: Cassandra) extends POAuth2Clients {

  implicit def rowToClient(row: Row): Option[POAuth2Client] = {

    Option(row) map { r =>
      val grantTypes = r.getSet("grant_types", classOf[String]).asScala
      POAuth2Client(
        r.getString("id"),
        Option(r.getString("secret")),
        r.getUUID("user_id"),
        Option(r.getString("redirect_uri")),
        Option(r.getString("scope")),
        Set(grantTypes.toList: _*))
    }
  }

  override def find(id: String, secret: Option[String] = None): Future[Option[POAuth2Client]] = {
    val q = QueryBuilder.select()
      .all().from("clients")
      .where(QueryBuilder.eq("id", id))
    secret.foreach { s => q.and(QueryBuilder.eq("secret", s)) }
    cassandra
      .executeAsync(q)
      .map(res => res.one())
  }

  override def save(client: POAuth2Client): Future[POAuth2Client] = {
    val q = QueryBuilder.update("clients")
      .`with`(QueryBuilder.set("user_id", client.userId))
      .and(QueryBuilder.set("grant_types", client.grantTypes.asJava))

    client.redirectUri.foreach(v => q.and(QueryBuilder.set("redirect_uri", v)))
    client.scope.foreach(v => q.and(QueryBuilder.set("scope", v)))

    q.where(QueryBuilder.eq("id", client.id))
    client.secret.foreach(v => q.where(QueryBuilder.eq("secret", v)))

    cassandra.executeAsync(q).map(res => client)
  }

}