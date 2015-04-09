package com.undeploy.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.databind.PropertyNamingStrategy

object Json {
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  mapper.registerModule(new JodaModule())
  mapper.setSerializationInclusion(Include.NON_NULL)
  mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
  mapper.enable(SerializationFeature.INDENT_OUTPUT)
  mapper.setPropertyNamingStrategy(
    PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);

  def toJson(value: Any): Array[Byte] = {
    mapper.writeValueAsBytes(value)
  }
}