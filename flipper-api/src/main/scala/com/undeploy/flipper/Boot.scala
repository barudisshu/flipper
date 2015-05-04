package com.undeploy.flipper.api

import scala.collection.JavaConversions.asScalaBuffer
import scala.concurrent.duration.DurationInt

import com.typesafe.config.ConfigFactory
import com.undeploy.cassandra.Cassandra
import com.undeploy.flipper.ApplicationContext
import com.undeploy.flipper.Events
import com.undeploy.flipper.OAuth2Clients
import com.undeploy.flipper.Passwords
import com.undeploy.flipper.Users
import com.undeploy.flipper.api.internal.ApiInternalActor
import com.undeploy.flipper.api.version1.ApiVersion1Actor
import com.undeploy.flipper.dao.CassandraEvents
import com.undeploy.flipper.dao.CassandraOAuth2Clients
import com.undeploy.flipper.dao.CassandraPasswords
import com.undeploy.flipper.dao.CassandraUsers
import com.undeploy.oauth2.CassandraOAuth2DataHandler

import akka.actor.ActorSystem
import akka.actor.Props
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http

object Boot extends App {

  val conf = ConfigFactory.load()

  val cassConf = conf.getConfig("cassandra")
  import collection.JavaConversions._
  val _cassandra = Cassandra.migrate(
    cassConf.getString("keyspace"),
    cassConf.getStringList("hosts"),
    cassConf.getInt("port"))

  val appContext = new ApplicationContext {
    val cassandra = _cassandra

    val pUsers = new CassandraUsers(cassandra)
    val users = Users(pUsers)

    val pPasswords = new CassandraPasswords(cassandra)
    val passwords = Passwords(pPasswords)

    val pClients = new CassandraOAuth2Clients(cassandra)
    val clients = OAuth2Clients(pClients)

    val oauth2Handler = new CassandraOAuth2DataHandler(users, passwords, clients, cassandra)

    val pEvents = new CassandraEvents(cassandra)
    val events = Events(pEvents)
  }

  implicit val system = ActorSystem("flipper-api")

  implicit val timeout = Timeout(5.seconds)

  val apiVersion1Service = system.actorOf(Props(new ApiVersion1Actor(appContext.oauth2Handler)), "api-version1-service")
  IO(Http) ? Http.Bind(apiVersion1Service, interface = "localhost", port = 8080)

  val apiInternalService = system.actorOf(Props(new ApiInternalActor(appContext)), "api-internal-service")
  IO(Http) ? Http.Bind(apiInternalService, interface = "localhost", port = 8090)
}
