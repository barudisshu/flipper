name := """flipper"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "org.pac4j" % "play-pac4j_scala2.11" % "1.3.0",
  "org.pac4j" % "pac4j-oauth" % "1.6.0",
  "com.chrisomeara" % "pillar_2.11" % "2.0.1",
  "com.datastax.cassandra" % "cassandra-driver-core" % "2.1.5",
  "com.nulab-inc" % "play2-oauth2-provider_2.11" % "0.13.2",
  "joda-time" % "joda-time" % "2.7",
  "commons-lang" % "commons-lang" % "2.6",
  "com.ibm.icu" % "icu4j" % "54.1.1",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.5.1",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % "2.5.2",
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "ch.qos.logback" % "logback-core" % "1.1.3",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "org.apache.cassandra" % "cassandra-all" % "2.1.4" % "test"
)
