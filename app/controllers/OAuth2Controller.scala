package controllers

import models._
import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import play.api.mvc.BodyParsers._
import play.api.libs.json.Json
import play.api.libs.json.Json._

import scala.concurrent.ExecutionContext.Implicits.global
import scalaoauth2.provider._

import controllers._

object OAuth2Controller extends Controller with OAuth2Provider {
  def accessToken = Action.async { implicit request =>
    issueAccessToken(Global.context().oauth2Handler)
  }
}


