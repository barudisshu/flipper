package com.undeploy.lang

import org.joda.time.DateTime
import org.joda.time.DateTimeZone

object Time {
  def timestamp() = DateTime.now(DateTimeZone.UTC)
}