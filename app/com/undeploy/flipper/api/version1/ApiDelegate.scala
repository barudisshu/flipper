package com.undeploy.flipper.api.version1

import scalaoauth2.provider.AuthInfo
import com.undeploy.flipper.User
import java.util.UUID
import org.joda.time.DateTime

case class ApiUser(
  email: String,
  id: UUID,
  locale: String,
  createdAt: DateTime,
  lastUpdate: DateTime)

object ApiDelegate {

  implicit def toApiUser(user: User): ApiUser = {
    ApiUser(
      user.email,
      user.id,
      user.locale.toString,
      user.createdAt,
      user.lastUpdate)
  }

  def me(authInfo: AuthInfo[User]): ApiUser = authInfo.user
}