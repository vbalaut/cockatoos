package com.datametica.cockatoos

import com.datametica.cockatoos.configuration.{Configuration, ConfigurationParser}
import com.datametica.cockatoos.metric.MetricSet
import com.datametica.cockatoos.session.Session
import org.apache.log4j.LogManager

/**
  * Cockatoos - runs Spark SQL queries on various data sources and exports the results
  */
object Cockatoos extends App {
  val log = LogManager.getLogger(this.getClass)
  log.info("Starting Cockatoos - Parsing configuration")
  val config: Configuration = ConfigurationParser.parse(args)
  Session.init(config)
  runMetrics

  def runMetrics(): Unit = {
    Session.getConfiguration.metrics.foreach(metricSetPath => {
      val metricSet = new MetricSet(metricSetPath)
      metricSet.run()
    })
  }

}
