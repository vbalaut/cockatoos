package com.datametica.cockatoos.calculators

import com.datametica.cockatoos.exceptions.CockatoosFailedStepException
import com.datametica.cockatoos.instrumentation.InstrumentationUtils
import com.datametica.cockatoos.metric.Metric
import com.datametica.cockatoos.session.Session
import org.apache.log4j.LogManager
import org.apache.spark.sql.DataFrame

class SqlStepCalculator(metric: Metric) extends Calculator {
  val log = LogManager.getLogger(this.getClass)
  lazy val successStepsCounter = InstrumentationUtils.createNewCounter(Array(metric.name, "successfulSteps"))
  lazy val failedStepsCounter = InstrumentationUtils.createNewCounter(Array(metric.name, "failedSteps"))

  override def calculate(): DataFrame = {
    val sqlContext = Session.getSparkSession.sqlContext

    var stepResult = sqlContext.emptyDataFrame
    for (step <- metric.steps) {
      try {
        log.info(s"Calculating step ${step.dataFrameName}")
        stepResult = step.actOnDataFrame(sqlContext)
        successStepsCounter.inc()
      } catch {
        case ex: Exception => {
          val errorMessage = s"Failed to calculate dataFrame: ${step.dataFrameName} on metric: ${metric.name}"
          failedStepsCounter.inc()
          if (Session.getConfiguration.continueOnFailedStep) {
            log.error(errorMessage, ex)
          } else {
            throw CockatoosFailedStepException(errorMessage, ex)
          }
        }
      }
      printStep(stepResult, step.dataFrameName)
    }
    stepResult
  }

  private def printStep(stepResult: DataFrame, stepName: String): Unit = {
    if (Session.getConfiguration.showPreviewLines > 0) {
      log.info(s"Previewing step: ${stepName}")
      try {
        stepResult.printSchema()
        stepResult.show(Session.getConfiguration.showPreviewLines, truncate = false)
      } catch {
        case ex: Exception => {
          log.warn(s"Couldn't print properly step ${stepName}")
        }
      }
    }

  }

}
