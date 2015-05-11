package com.undeploy.flipper.api.version1

import akka.actor.Actor
import spray.routing._
import spray.http._
import spray.http.MediaTypes._
import spray.httpx.marshalling.ToResponseMarshallable.isMarshallable
import spray.routing.Directive.pimpApply
import com.undeploy.oauth2.OAuth2HttpService
import com.undeploy.flipper.User
import scalaoauth2.provider.AuthInfo
import scala.concurrent._
import ExecutionContext.Implicits.global
import spray.routing.authentication.BasicAuth
import com.undeploy.flipper.Users
import com.undeploy.spray.Jackson._

trait ApiService extends HttpService with OAuth2HttpService[User] {

  val users: Users

  val apiVersion1 =
    path("me") {
      authenticate(oauth2Auth) { authInfo =>
        get {
          complete {
            users.findById(authInfo.user.id)
          }
        }
      }
    }
}