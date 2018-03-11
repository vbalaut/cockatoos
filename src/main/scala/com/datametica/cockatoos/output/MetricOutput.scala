package com.datametica.cockatoos.output

import com.datametica.cockatoos.metric.config.Output

case class MetricOutput(outputConfig: Output, metricName: String) {
  val writer: MetricOutputWriter = MetricOutputWriterFactory.get(outputConfig, metricName)
}
