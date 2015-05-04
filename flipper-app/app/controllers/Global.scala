package controllers

import play.api._
import com.undeploy.play.pac4j._

object Global extends GlobalSettings {

  override def onStart(app: Application) = {
    Pac4j.init(app)
  }

  override def onStop(app: Application) = {
  }
}