package com.undeploy.flipper.api.version1

import com.undeploy.oauth2.OAuth2HttpService
import scalaoauth2.provider.DataHandler
import akka.actor.Actor
import com.undeploy.flipper.User
import spray.http.MediaTypes._
import com.undeploy.flipper.Users

class ApiVersion1Actor(_dataHandler: DataHandler[User], _users: Users) extends Actor with ApiService {

  val users = _users
  val dataHandler = _dataHandler

  def actorRefFactory = context

  val root = path("") {
    get {
      respondWithMediaType(`text/html`) {
        complete {
          <html>
            <body>
              <h1>Public API</h1>
            </body>
          </html>
        }
      }
    }
  }

  def receive = runRoute(root ~ apiVersion1 ~ oauth2)
}
