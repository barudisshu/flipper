package controllers.api

import models._
import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.mvc.BodyParsers._
import scala.concurrent.ExecutionContext.Implicits.global
import scalaoauth2.provider._
import controllers._
import scala.concurrent.Future
import com.undeploy.json.Json
import com.undeploy.flipper.api.version1.ApiDelegate

object ApiVersion1Controller extends Controller with OAuth2Provider {

  def me = Action.async { implicit request =>
    authorize(Global.context().oauth2Handler) { authInfo =>
      ApiDelegate.me(authInfo).map { user =>
        Ok(Json.toJson(user))
      }
    }
  }
}


