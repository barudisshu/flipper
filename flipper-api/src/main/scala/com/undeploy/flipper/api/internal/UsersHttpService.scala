package com.undeploy.flipper.api.internal

import akka.actor.Actor
import spray.routing._
import spray.http._
import spray.http.MediaTypes._
import spray.httpx.marshalling.ToResponseMarshallable.isMarshallable
import spray.routing.Directive.pimpApply
import com.undeploy.flipper.Users
import com.undeploy.flipper.User
import com.undeploy.spray.Jackson._

trait UsersHttpService extends HttpService {

  val users: Users

  val apiUsers =
    path("users" / Segment) { email =>
      put {
        entity(as[User]) { user =>
          complete {
            users.save(user.copy(email = email))
          }
        }
      }
    }
}