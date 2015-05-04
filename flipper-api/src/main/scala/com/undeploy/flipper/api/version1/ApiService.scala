package com.undeploy.flipper.api.version1

import akka.actor.Actor
import spray.routing._
import spray.http._
import spray.http.MediaTypes._
import spray.httpx.marshalling.ToResponseMarshallable.isMarshallable
import spray.routing.Directive.pimpApply
 
trait ApiService extends HttpService {

  val apiVersion1 =
    path("") {
      get {
        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
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
}