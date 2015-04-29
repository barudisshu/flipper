package com.undeploy.lang

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.ISODateTimeFormat
import java.util.Date

object Time {
  def timestamp() = DateTime.now(DateTimeZone.UTC)

  def timestamp(instant: Long) = new DateTime(instant, DateTimeZone.UTC)

  def toISODate(dateTime: DateTime): String = ISODateTimeFormat.date().print(dateTime)

  def toISODate(date: Date): String = toISODate(toDateTime(date))

  def toDateTime(date: Date) = {
    Option(date).map(d => new DateTime(d.getTime, DateTimeZone.UTC)).orNull
  }

  def toDate(dateTime: DateTime) = {
    Option(dateTime).map(_.toDateTime(DateTimeZone.UTC).toDate()).orNull
  }

}