package com.undeploy.flipper.dao

import scala.concurrent._
import ExecutionContext.Implicits.global
import com.undeploy.cassandra.Cassandra
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.Row
import com.undeploy.lang.Converters._
import java.util.UUID
import com.undeploy.flipper.PEvent
import com.undeploy.flipper.PEvents

class CassandraEvents(cassandra: Cassandra) extends PEvents {

  implicit def rowToUser(row: Row): Option[PEvent] = {
    Option(row) map { r =>
      PEvent()
    }
  }

}