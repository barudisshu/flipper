name := "flipper-api"

version := "1.0-SNAPSHOT"

scalaVersion  := "2.11.6"

libraryDependencies ++= Seq(
  "io.spray" % "spray-can_2.11" % "1.3.1",
  "io.spray" % "spray-routing_2.11" % "1.3.1",
  "io.spray" % "spray-json_2.11" % "1.3.1",
  "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.3",
  "com.typesafe.akka" % "akka-actor_2.11" % "2.3.9",
  "org.pac4j" % "play-pac4j_scala2.11" % "1.3.0",
  "com.chrisomeara" % "pillar_2.11" % "2.0.1",
  "com.datastax.cassandra" % "cassandra-driver-core" % "2.1.5",
  "com.nulab-inc" % "scala-oauth2-core_2.11" % "0.13.3",
  "joda-time" % "joda-time" % "2.7",
  "org.apache.commons" % "commons-lang3" % "3.1",
  "com.ibm.icu" % "icu4j" % "54.1.1",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.5.1",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % "2.5.2",
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "ch.qos.logback" % "logback-core" % "1.1.3",
  "com.fasterxml.uuid" % "java-uuid-generator" % "3.1.4",
  "com.nimbusds" % "nimbus-jose-jwt" % "3.10",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "org.apache.cassandra" % "cassandra-all" % "2.1.4" % "test"
)

Revolver.settings
