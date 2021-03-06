package com.datametica.cockatoos.metric

import com.datametica.cockatoos.calculators.SqlStepCalculator
import com.datametica.cockatoos.exceptions.CockatoosWriteFailedException
import com.datametica.cockatoos.instrumentation.InstrumentationUtils
import com.datametica.cockatoos.session.Session
import com.datametica.cockatoos.utils.FileUtils
import org.apache.log4j.LogManager
import org.apache.spark.groupon.metrics.SparkGauge

object MetricSet {
  type metricSetCallback = (String) => Unit
  private var beforeRun: Option[metricSetCallback] = None
  private var afterRun: Option[metricSetCallback] = None

  def setBeforeRunCallback(callback: metricSetCallback) {
    beforeRun = Some(callback)
  }

  def setAfterRunCallback(callback: metricSetCallback) {
    afterRun = Some(callback)
  }
}

class MetricSet(metricSet: String) {
  val log = LogManager.getLogger(this.getClass)

  val metrics: Seq[Metric] = parseMetrics(metricSet)

  def parseMetrics(metricSet: String): Seq[Metric] = {
    log.info(s"Starting to parse metricSet")
    val metricsToCalculate = FileUtils.getListOfFiles(metricSet)
    metricsToCalculate.filter(MetricFile.isValidFile(_)).map(new MetricFile(_).metric)
  }

  def run() {
    MetricSet.beforeRun match {
      case Some(callback) => callback(metricSet)
      case None =>
    }

    metrics.foreach(metric => {
      lazy val timer = InstrumentationUtils.createNewGauge(Array(metric.name, "timer"))
      val startTime = System.nanoTime()

      val calculator = new SqlStepCalculator(metric)
      calculator.calculate()
      write(metric)

      val endTime = System.nanoTime()
      val elapsedTimeInNS = (endTime - startTime)
      timer.set(elapsedTimeInNS)
    })

    MetricSet.afterRun match {
      case Some(callback) => callback(metricSet)
      case None =>
    }
  }

  def write(metric: Metric) {
    metric.outputs.foreach(output => {
      val sparkSession = Session.getSparkSession
      val dataFrameName = output.outputConfig.dataFrameName
      val dataFrame = sparkSession.table(dataFrameName)
      dataFrame.cache()

      lazy val counterNames = Array(metric.name, dataFrameName, output.outputConfig.outputType.toString, "counter")
      lazy val dfCounter: SparkGauge = InstrumentationUtils.createNewGauge(counterNames)
      dfCounter.set(dataFrame.count())

      log.info(s"Starting to Write results of ${dataFrameName}")
      try {

        output.writer.write(dataFrame)
      } catch {
        case ex: Exception => {
          throw CockatoosWriteFailedException(s"Failed to write dataFrame: " +
            s"$dataFrameName to output: ${output.outputConfig.outputType} on metric: ${metric.name}", ex)
        }
      }
    })

  }


}
