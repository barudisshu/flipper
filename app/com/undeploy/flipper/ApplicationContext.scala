package com.undeploy.flipper

import com.undeploy.cassandra.Cassandra
import scalaoauth2.provider.DataHandler

trait ApplicationContext {
  val cassandra : Cassandra
  val users : Users
  val passwords : Passwords
  val clients: OAuth2Clients
  val oauth2Handler : DataHandler[User]
}