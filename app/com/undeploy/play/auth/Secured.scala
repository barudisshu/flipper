package com.undeploy.play.auth

import org.pac4j.core.profile.CommonProfile

import org.pac4j.play.scala.ScalaController
import play.api.mvc.Action
import play.api.mvc.AnyContent
import org.pac4j.play.Config
import scala.concurrent.Future

abstract class Secured extends ScalaController {

  def Authenticated(
    authenticated: CommonProfile => Action[AnyContent],
    notAuthenticated: Action[AnyContent]) = Action.async(parse.anyContent) { request =>
    val profile = getUserProfile(request)
    if (profile == null) {
      notAuthenticated(request)
    } else {
      authenticated(profile)(request)
    }
  }

  def Authenticated(authenticated: CommonProfile => Action[AnyContent]): Action[AnyContent] = {
    Authenticated(authenticated, Action { request =>
      Unauthorized(Config.getErrorPage401()).as(HTML)
    })
  }
}