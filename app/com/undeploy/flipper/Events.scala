package com.undeploy.flipper

import java.util.Date
import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import org.joda.time.DateTime

import com.undeploy.json.Json
import com.undeploy.lang.ID
import com.undeploy.lang.Time

case class PEvent(
  id: UUID,
  schemaId: Option[UUID],
  bucketId: UUID,
  date: String,
  collectedAt: Date,
  timestamp: Date,
  fields: Array[Byte])

trait PEvents {
  def insert(event: PEvent): Future[PEvent]
  def findById(id: UUID): Future[Option[PEvent]]
  def delete(event: PEvent): Future[PEvent]
}

case class Event(
  id: UUID,
  schemaId: Option[UUID],
  bucketId: UUID,
  createdAt: DateTime,
  collectedAt: DateTime,
  timestamp: DateTime,
  fields: Map[String, Any])

class Events(pEvents: PEvents) {

  implicit def toPEvent(event: Event): PEvent = {
    Option(event) map { e =>
      PEvent(
        e.id,
        e.schemaId,
        e.bucketId,
        Time.toISODate(e.timestamp),
        Time.toDate(e.collectedAt),
        Time.toDate(e.timestamp),
        Json.toBytes(e.fields))
    } orNull
  }

  implicit def fromPEvent(event: PEvent): Event = {
    Option(event) map { e =>
      Event(
        e.id,
        e.schemaId,
        e.bucketId,
        ID.toDateTime(e.id),
        Time.toDateTime(e.collectedAt),
        Time.toDateTime(e.timestamp),
        Json.parse(e.fields))
    } orNull
  }

  implicit def fromPEvent(event: Option[PEvent]): Option[Event] = {
    event.map(x => x)
  }

  def publish(event: Event): Future[Event] = {
    val id = ID.time()
    val e = event.copy(id = id, createdAt = ID.toDateTime(id))
    pEvents.insert(e).map(x => x)
  }

  def findById(id: UUID): Future[Option[Event]] = {
    pEvents.findById(id).map(x => x)
  }

  def delete(event: Event): Future[Event] = {
    pEvents.delete(event).map(x => x)
  }

}

object Events {
  def apply(pEvents: PEvents) = new Events(pEvents)
}

