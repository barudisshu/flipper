package com.undeploy.flipper.api.version1

import scalaoauth2.provider.AuthInfo
import com.undeploy.flipper.User
import java.util.UUID
import org.joda.time.DateTime
import scala.concurrent.Future
import com.undeploy.flipper.Event
import controllers.Global
import scala.concurrent._
import ExecutionContext.Implicits.global

case class ApiUser(
  email: String,
  id: UUID,
  locale: String,
  createdAt: DateTime,
  lastUpdate: DateTime)

case class ApiEvent(
  id: UUID,
  schemaId: Option[UUID],
  bucketId: UUID,
  createdAt: DateTime,
  collectedAt: DateTime,
  timestamp: DateTime,
  fields: Map[String, Any])

object Implicits {
  implicit def toApiUser(user: User): ApiUser =
    ApiUser(
      user.email,
      user.id,
      user.locale.toString,
      user.createdAt,
      user.lastUpdate)

  implicit def toEvent(event: ApiEvent): Event =
    Event(
      event.id,
      event.schemaId,
      event.bucketId,
      event.createdAt,
      event.collectedAt,
      event.timestamp,
      event.fields)
}

object ApiDelegate {

  import Implicits._

  def me(authInfo: AuthInfo[User]): Future[ApiUser] = {
    Future.successful(authInfo.user)
  }

  def publishEvent(
    bucketId: UUID,
    event: ApiEvent,
    authInfo: AuthInfo[User]): Future[UUID] = {
    Global.context().events.publish(event.copy(bucketId = bucketId)).map(_.id)
  }

}