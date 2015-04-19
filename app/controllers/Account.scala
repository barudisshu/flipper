package controllers

import play.api._
import play.api.mvc._
import com.undeploy.play.auth.Secured

object Account extends Secured {

  def account = Authenticated { profile =>
    Action { request =>
      Ok("ciao")
    }
  }

}