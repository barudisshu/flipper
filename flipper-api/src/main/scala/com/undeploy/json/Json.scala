package com.undeploy.json

import com.fasterxml.jackson.databind.ObjectMapper

import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.core.`type`.TypeReference
import scala.reflect.ClassTag
import scala.reflect._
import com.undeploy.lang.Scala

object Json {
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  mapper.registerModule(new JodaModule())
  mapper.setSerializationInclusion(Include.NON_NULL)
  mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
  mapper.enable(SerializationFeature.INDENT_OUTPUT)
  mapper.setPropertyNamingStrategy(
    PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);

  def toBytes(value: Any): Array[Byte] = {
    mapper.writeValueAsBytes(value)
  }

  def toString(value: Any): String = {
    mapper.writeValueAsString(value)
  }

  def parse(json: Array[Byte]): Map[String, Any] = {
    mapper.readValue(json, new TypeReference[Map[String, Any]]() {});
  }

  def parse[T](json: Array[Byte], cls: Class[T]): T = {
    mapper.readValue(json, cls)
  }

  def parse[T: ClassTag](json: String): T = {
    mapper.readValue[T](json, Scala.getClass[T])
  }
}