package com.datametica.cockatoos.output

import org.apache.spark.sql.SparkSession

trait MetricOutputSession {
  def addToSparkSession(sparkSession: SparkSession): Unit = {}
}
