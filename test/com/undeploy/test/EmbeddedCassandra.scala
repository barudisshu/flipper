package com.undeploy.test

import org.apache.cassandra.config.ConfigurationLoader
import org.apache.cassandra.config.Config
import org.apache.cassandra.dht.Murmur3Partitioner
import java.nio.file.Files
import org.apache.cassandra.config.SeedProviderDef
import org.apache.cassandra.locator.SimpleSeedProvider
import java.util.LinkedHashMap
import java.util.Collections
import java.net.ServerSocket
import org.apache.cassandra.service.CassandraDaemon
import org.apache.cassandra.dht.Murmur3Partitioner
import org.apache.cassandra.locator.SimpleSeedProvider
import org.apache.cassandra.dht.Murmur3Partitioner
import org.apache.cassandra.locator.SimpleSeedProvider
import org.apache.cassandra.config.Config.CommitLogSync

class CassandraServer(daemon: CassandraDaemon) {

  def start = daemon.start()

  def stop = daemon.stop()
}

object CassandraServer {

  def apply() = {
    System.setProperty("cassandra.config.loader", classOf[CassandraServerConfigurationLoader].getName)
    val daemon = new CassandraDaemon()
    daemon.init(null)
    new CassandraServer(daemon)
  }

}

class CassandraServerConfigurationLoader extends ConfigurationLoader {

  override def loadConfig(): Config = {
    val config = new Config()
    config.commitlog_sync = CommitLogSync.periodic
    config.commitlog_sync_period_in_ms = 10000
    config.partitioner = classOf[Murmur3Partitioner].getName
    config.endpoint_snitch = "SimpleSnitch"

    val cassandraCommitTmpPath = Files.createTempDirectory("cassandra_commit_tmp").toString
    config.commitlog_directory = cassandraCommitTmpPath

    val cassandraDataTmpPath = Files.createTempDirectory("cassandra_data_tmp").toString
    config.data_file_directories = Array(cassandraDataTmpPath)

    val cassandraCacheTmpPath = Files.createTempDirectory("cassandra_cache_tmp").toString
    config.saved_caches_directory = cassandraCacheTmpPath

    config.seed_provider = seedProviderDef

    config.listen_address = "localhost"

    val s = new ServerSocket(0)
    config.native_transport_port = 9042 //s.getLocalPort
    config.start_native_transport = true

    config
  }

  def seedProviderDef = {
    val providerMap = new LinkedHashMap[String, Any]
    providerMap.put("class_name", classOf[SimpleSeedProvider].getName)
    val seedsMap = new LinkedHashMap[String, Any]
    seedsMap.put("seeds", "127.0.0.1")
    val params = Collections.singletonList(seedsMap)
    providerMap.put("parameters", params)
    new SeedProviderDef(providerMap)
  }
}