package com.undeploy.lang

import scala.reflect.ClassTag
import scala.reflect._

object Scala {
  def getClass[T: ClassTag]() : Class[T] = classTag[T].runtimeClass.asInstanceOf[Class[T]]
}