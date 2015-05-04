package com.undeploy.flipper.api.internal

import akka.actor.Actor

import com.undeploy.flipper.Users
import spray.http.MediaTypes._
import com.undeploy.flipper.ApplicationContext

class ApiInternalActor(context: ApplicationContext) extends Actor with UsersHttpService {

  val users = context.users

  def actorRefFactory = context

  val root = path("") {
    get {
      respondWithMediaType(`text/html`) {
        complete {
          <html>
            <body>
              <h1>Internal API</h1>
            </body>
          </html>
        }
      }
    }
  }

  def receive = runRoute(root ~ apiUsers)
}
