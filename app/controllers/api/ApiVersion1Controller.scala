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
import java.util.UUID
import com.undeploy.flipper.api.version1.ApiEvent

object ApiVersion1Controller extends Controller with OAuth2Provider {

  import com.undeploy.flipper.api.version1.Implicits._

  def me = Action.async { implicit request =>
    authorize(Global.context().oauth2Handler) { authInfo =>
      ApiDelegate.me(authInfo).map { user =>
        Ok(Json.toBytes(user))
      }
    }
  }

  def publishEvent(bucketId: UUID) = Action.async { implicit request =>
    authorize(Global.context().oauth2Handler) { authInfo =>
      request.body.asJson
        .map(j => j.toString().getBytes())
        .map(Json.parse(_, classOf[ApiEvent]))
        .map(ApiDelegate.publishEvent(bucketId, _, authInfo)
          .map(eventId => Ok(eventId.toString())))
        .getOrElse(Future.successful(Results.BadRequest))
    }
  }

}


