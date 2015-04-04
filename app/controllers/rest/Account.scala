package controllers.rest

import play.api._

import play.api.mvc._
import org.pac4j.play.scala.ScalaController
import org.pac4j.core.profile.CommonProfile
import scala.concurrent.Future
import org.pac4j.play.Config
import com.undeploy.play.auth.Secured

object Account extends Secured {

  def account = Authenticated { profile =>
    Action { request =>
      Ok("ciao")
    }
  }

}