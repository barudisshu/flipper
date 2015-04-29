package com.undeploy.lang

import java.util.Date
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.util.Locale
import com.ibm.icu.util.ULocale

object Converters {

  implicit def ToString(locale: Locale): String = {
    Option(locale).map(_.toString()).orNull
  }

  implicit def ToLocale(locale: String): Locale = {
    Option(locale).map(loc => new ULocale(locale).toLocale()).orNull
  }
}