package com.undeploy.lang

import com.fasterxml.uuid.Generators
import java.util.UUID
import org.joda.time.DateTime

object ID {

  def time() = {
    Generators.timeBasedGenerator().generate()
  }

  def toDateTime(id: UUID) = {
    Option(id).map(id => new DateTime(id.timestamp() / 10000L + 12219292800L)).orNull
  }
}