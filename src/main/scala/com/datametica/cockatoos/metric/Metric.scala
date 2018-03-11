package com.datametica.cockatoos.metric

import java.io.File

import com.datametica.cockatoos.metric.config.MetricConfig
import com.datametica.cockatoos.metric.step.{Sql, StepAction}
import com.datametica.cockatoos.output.MetricOutput

class Metric(metricConfig: MetricConfig, metricDir: File, metricName: String) {
  val name: String = metricName
  val steps: List[StepAction] = metricConfig.steps.map(stepConfig => Sql(stepConfig.getSqlQuery(metricDir), stepConfig.dataFrameName))
  val outputs: List[MetricOutput] = metricConfig.output.map(MetricOutput(_, name))
}


