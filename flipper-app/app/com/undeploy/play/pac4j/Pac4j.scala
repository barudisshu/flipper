package com.undeploy.play.pac4j

import play.api.Configuration
import org.pac4j.oauth.client.Google2Client
import org.pac4j.oauth.client.Google2Client.Google2Scope
import org.pac4j.core.client.Clients
import play.api.Play
import play.api.Application
import org.pac4j.play.Config

object Pac4j {
  def init(app: Application): Unit = {
    val clients = for {
      conf <- app.configuration.getConfig("pac4j.google")      
      id <- conf.getString("client_id")
      secret <- conf.getString("client_secret")
      baseUrl <- app.configuration.getString("pac4j.base_url")
    } yield {
      val client = new Google2Client(id, secret)
      client.setScope(Google2Scope.EMAIL)
      new Clients(s"$baseUrl/callback", client)
    }
    clients.foreach { Config.setClients(_) }
  }

}