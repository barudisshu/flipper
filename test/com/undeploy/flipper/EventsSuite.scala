package com.undeploy.flipper

import org.apache.avro.SchemaBuilder

import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericDatumWriter
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.EncoderFactory
import org.apache.commons.io.output.ByteArrayOutputStream
import org.scalatest.Finders
import org.scalatest.FunSuite
import com.undeploy.test.TestApplicationContext._
import com.undeploy.test.Faker
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent._
import ExecutionContext.Implicits.global
import java.util.UUID

class EventsSuite extends FunSuite {

  test("an event is saved") {
    val event = Faker.event()

    val saved = Await.result(
      events.publish(event).flatMap(e =>
        events.findById(e.id)), 1 seconds).get

    assertResult(event.id)(saved.id)
    assertResult(event.schemaId)(saved.schemaId)
    assertResult(event.bucketId)(saved.bucketId)
    assert(saved.createdAt.isAfter(event.createdAt))
    assertResult(event.collectedAt)(saved.collectedAt)
    assertResult(event.timestamp)(saved.timestamp)
    assertResult(event.fields.keys)(saved.fields.keys)
  }

  test("an event is deleted") {
    val event = Faker.event()

    val found = Await.result(
      events.publish(event)
        .flatMap(events.delete(_))
        .flatMap(e => events.findById(e.id)), 1 seconds)

    assertResult(None)(found)
  }

}