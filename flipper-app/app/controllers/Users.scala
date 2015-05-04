package controllers

import java.util.Locale
import org.pac4j.core.profile.CommonProfile
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import scala.concurrent.Future

class Users(val apiDelegate: ApiDelegate) {

  def saveUser(locale: Locale, profile: CommonProfile): Future[WSResponse] = {
    apiDelegate.internal(s"users/${profile.getEmail}")
      .put(Json.obj("locale" -> locale.toString()))
  }

}