package com.undeploy.cassandra

import java.io.Closeable
import java.io.File
import org.slf4j.LoggerFactory
import com.chrisomeara.pillar.Migrator
import com.chrisomeara.pillar.Registry
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Row
import com.datastax.driver.core.Statement
import com.datastax.driver.core.ResultSetFuture
import scala.concurrent.Promise
import scala.concurrent.Future
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures

class Cassandra(keyspace: Option[String] = None, host: Seq[String], port: Int) extends Closeable {

  val logger = LoggerFactory.getLogger(this.getClass)

  val cluster = Cluster.builder().addContactPoints(host: _*).withPort(port).build()
  lazy val keyspaceSession = keyspace.map { cluster.connect(_) } getOrElse (cluster.connect())

  implicit def resultSetFutureToScala(f: ResultSetFuture): Future[ResultSet] = {
    val p = Promise[ResultSet]()
    Futures.addCallback(f,
      new FutureCallback[ResultSet] {
        def onSuccess(r: ResultSet) = p success r
        def onFailure(t: Throwable) = p failure t
      })
    p.future
  }

  def executeOne(query: Statement): Option[Row] = {
    logger.debug(s"Executing query $query")
    val row = keyspaceSession.execute(query).one()
    if (row == null) None else Some(row)
  }

  def execute(query: Statement): ResultSet = {
    logger.debug(s"Executing query $query")
    keyspaceSession.execute(query)
  }

  def execute(query: String, params: AnyRef*): ResultSet = {
    logger.debug(s"Executing query $query")
    keyspaceSession.execute(query, params: _*)
  }

  def executeAsync(query: String, params: AnyRef*): Future[ResultSet] = {
    keyspaceSession.executeAsync(query, params: _*)
  }

  def executeAsync(query: Statement): Future[ResultSet] = {
    keyspaceSession.executeAsync(query)
  }

  override def close = {
    keyspaceSession.close()
    cluster.close()
  }
}
object Cassandra {
  def apply(host: Seq[String], port: Int) =
    new Cassandra(None, host, port)

  def apply(keyspace: String, host: Seq[String], port: Int) =
    new Cassandra(Some(keyspace), host, port)

  def migrate(keyspace: String, host: Seq[String], port: Int): Cassandra = {
    val registry = Registry.fromDirectory(new File(getClass.getResource("/migrations").toURI))
    val migrator = Migrator(registry)
    var cassandra: Cassandra = null
    try {
      cassandra = Cassandra(host, port)
      migrator.initialize(cassandra.keyspaceSession, keyspace)
    } finally {
      if (cassandra != null) cassandra.close();
    }

    cassandra = Cassandra(keyspace, host, port)
    migrator.migrate(cassandra.keyspaceSession)
    cassandra
  }
}