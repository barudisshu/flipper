package com.undeploy.flipper.api.version1

import com.undeploy.oauth2.OAuth2HttpService
import scalaoauth2.provider.DataHandler
import akka.actor.Actor
import com.undeploy.flipper.User

class ApiVersion1Actor(_dataHandler: DataHandler[User]) extends Actor with ApiService with OAuth2HttpService[User] {

  val dataHandler = _dataHandler

  def actorRefFactory = context

  def receive = runRoute(apiVersion1 ~ oauth2)
}
