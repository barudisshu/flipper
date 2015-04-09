package com.undeploy.test

import com.undeploy.cassandra.Cassandra
import com.undeploy.flipper.ApplicationContext
import com.undeploy.flipper.CassandraUsers
import com.undeploy.flipper.Passwords
import com.undeploy.flipper.Users
import com.undeploy.flipper.OAuth2Clients
import com.undeploy.flipper.CassandraPasswords
import com.undeploy.flipper.CassandraOAuth2Clients

object TestApplicationContext extends ApplicationContext {
  val cassandra = Cassandra.migrate("flipper_test", Seq("127.0.0.1"), 9042)

  val pUsers = new CassandraUsers(cassandra)
  val users = Users(pUsers)

  val pPasswords = new CassandraPasswords(cassandra)
  val passwords = Passwords(pPasswords)

  val pClients = new CassandraOAuth2Clients(cassandra)
  val clients = OAuth2Clients(pClients)

  val oauth2Handler = null

}