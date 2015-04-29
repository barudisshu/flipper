package com.undeploy.flipper.dao

import java.nio.ByteBuffer
import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import com.datastax.driver.core.Row
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.utils.Bytes
import com.undeploy.cassandra.Cassandra
import com.undeploy.flipper.PEvent
import com.undeploy.flipper.PEvents
import com.undeploy.lang.Time

class CassandraEvents(cassandra: Cassandra) extends PEvents {

  implicit def rowToUser(row: Row): Option[PEvent] = {
    Option(row) map { r =>
      val timestamp = r.getDate("timestamp")
      PEvent(
        r.getUUID("id"),
        Option(r.getUUID("schema_id")),
        r.getUUID("bucket_id"),
        Time.toISODate(timestamp),
        r.getDate("collected_at"),
        timestamp,
        Bytes.getArray(r.getBytes("fields")))
    }
  }

  override def insert(event: PEvent): Future[PEvent] = {
    val q = QueryBuilder.insertInto("events")
      .value("id", event.id)
      .value("bucket_id", event.bucketId)
      .value("date", event.date)
      .value("collected_at", event.collectedAt)
      .value("timestamp", event.timestamp)
      .value("fields", ByteBuffer.wrap(event.fields))
    event.schemaId.foreach(q.value("schema_id", _))
    cassandra.executeAsync(q).map(res => event)
  }

  override def findById(id: UUID): Future[Option[PEvent]] = {
    cassandra
      .executeAsync(QueryBuilder.select()
        .all().from("events")
        .where(QueryBuilder.eq("id", id)))
      .map(res => res.one())
  }

  override def delete(event: PEvent): Future[PEvent] = {
    cassandra
      .executeAsync(QueryBuilder.delete()
        .from("events")
        .where(QueryBuilder.eq("bucket_id", event.bucketId))
        .and(QueryBuilder.eq("date", event.date))
        .and(QueryBuilder.eq("timestamp", event.timestamp))
        .and(QueryBuilder.eq("id", event.id)))
      .map(res => event)
  }

}