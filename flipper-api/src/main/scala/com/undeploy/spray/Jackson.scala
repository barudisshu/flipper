package com.undeploy.spray

import spray.httpx.unmarshalling.Unmarshaller
import spray.http.MediaTypes
import spray.http.HttpEntity
import spray.http.HttpCharsets
import com.undeploy.json.Json
import spray.httpx.marshalling.Marshaller
import spray.http.ContentTypes
import scala.reflect.ClassTag
import scala.reflect._
import com.undeploy.flipper.User

object Jackson {

  implicit def jacksonJsonUnmarshaller[T: ClassTag] =
    Unmarshaller[T](MediaTypes.`application/json`) {
      case x: HttpEntity.NonEmpty =>
        val jsonSource = x.asString(defaultCharset = HttpCharsets.`UTF-8`)
        Json.parse[T](jsonSource)
    }

  implicit def jacksonJsonMarshaller[T <: AnyRef] =
    Marshaller.delegate[T, String](ContentTypes.`application/json`)(Json.toString(_))
}