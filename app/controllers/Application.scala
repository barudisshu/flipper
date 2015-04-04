package controllers

import scala.concurrent._
import ExecutionContext.Implicits.global
import play.api._
import play.api.mvc._
import org.pac4j.core.profile._
import org.pac4j.play._
import org.pac4j.play.scala._
import play.api.libs.json.Json
import com.undeploy.play.auth.Secured
import com.undeploy.flipper.User
import play.api.i18n.Lang

object Application extends Secured {

  def index = Authenticated(profile =>
    Action.async { request =>
      val lang = request2lang(request)
      Global.context().users
        .save(User(profile.getEmail, null, lang.toLocale, null, null))
        .map { u => Ok(views.html.index(profile)) }
    },
    Action { request =>
      val newSession = getOrCreateSessionId(request)
      val urlGoogle = getRedirectAction(request, newSession, "Google2Client").getLocation()
      Ok(views.html.login(urlGoogle)).withSession(newSession)
    })

}