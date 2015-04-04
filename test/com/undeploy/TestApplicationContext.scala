package com.undeploy

import com.undeploy.cassandra.Cassandra
import com.undeploy.flipper.Users
import com.undeploy.flipper.ApplicationContext

object TestApplicationContext extends ApplicationContext {
  val cassandra = Cassandra.migrate("flipper_test", Seq("127.0.0.1"), 9042)  
}