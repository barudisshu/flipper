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

case class PEvent()

trait PEvents {
}

case class Event()

class Events(pEvents: PEvents) {

  implicit def toPEvent(Event: Event): PEvent = {
    Option(Event) map { u =>
      PEvent()
    } orNull
  }

  implicit def fromPEvent(event: PEvent): Event = {
    Option(event) map { u =>
      Event()
    } orNull
  }

  implicit def fromPEvent(event: Option[PEvent]): Option[Event] = {
    event.map { x => x }
  }

}

object Events {
  def apply(pEvents: PEvents) = new Events(pEvents)
}

