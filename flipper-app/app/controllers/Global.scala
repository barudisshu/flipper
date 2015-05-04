package controllers

import play.api._
import com.undeploy.play.pac4j._
import java.net.URL
import play.api.libs.ws.WS
import play.api.Play.current
import play.api.mvc.RequestHeader
import play.api.mvc.Action
import play.api.mvc.Result
import scala.concurrent.Future

class ApiDelegate(val internalBaseUrl: URL, val publicBaseUrl: URL) {

  def internal(path: String) = reqHolder(internalBaseUrl, path)

  def public(path: String) = reqHolder(publicBaseUrl, path)

  private def reqHolder(base: URL, path: String) =
    WS.url(s"${base}/$path")
      .withHeaders("Accept" -> "application/json")
}

object Global extends GlobalSettings {

  var users: Users = _

  override def onStart(app: Application) = {
    val internal = app.configuration
      .getString("api.internal")
      .getOrElse("http://localhost:8090")
    val public = app.configuration
      .getString("api.public")
      .getOrElse("http://localhost:8080")
    val apiDelegate = new ApiDelegate(new URL(internal), new URL(public))

    users = new Users(apiDelegate)

    Pac4j.init(app)
  }

  override def onStop(app: Application) = {
  }
}