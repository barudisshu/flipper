package com.undeploy.flipper

import com.undeploy.cassandra.Cassandra

trait ApplicationContext {
  val cassandra : Cassandra
  val users : Users
}