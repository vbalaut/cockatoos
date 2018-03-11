package com.datametica.cockatoos.output

import com.datametica.cockatoos.exceptions.CockatoosException
import com.datametica.cockatoos.metric.config.Output
import com.datametica.cockatoos.output.writers.cassandra.CassandraOutputWriter
import com.datametica.cockatoos.output.writers.csv.CSVOutputWriter
import com.datametica.cockatoos.output.writers.instrumentation.InstrumentationOutputWriter
import com.datametica.cockatoos.output.writers.jdbc.{JDBCOutputWriter, JDBCQueryWriter}
import com.datametica.cockatoos.output.writers.json.JSONOutputWriter
import com.datametica.cockatoos.output.writers.parquet.ParquetOutputWriter
//import com.datametica.cockatoos.output.writers.redis.RedisOutputWriter
//import com.datametica.cockatoos.output.writers.redshift.RedshiftOutputWriter
import com.datametica.cockatoos.output.writers.segment.SegmentOutputWriter
import com.datametica.cockatoos.session.Session

object MetricOutputWriterFactory {
  def get(outputConfig: Output, metricName: String): MetricOutputWriter = {
    val output = Session.getConfiguration.output
    val metricOutputOptions = outputConfig.outputOptions
    val outputType = OutputType.withName(outputConfig.outputType)
    val metricOutputWriter = outputType match {
        //TODO: move casting into the writer class
      case OutputType.Cassandra => new CassandraOutputWriter(metricOutputOptions
        .asInstanceOf[Map[String, String]]) //TODO add here cassandra from session
     // case OutputType.Redshift => new RedshiftOutputWriter(metricOutputOptions
      // .asInstanceOf[Map[String, String]], output.redshift)
     // case OutputType.Redis => new RedisOutputWriter(metricOutputOptions
     //  .asInstanceOf[Map[String, String]]) //TODO add here redis from session
      case OutputType.Segment => new SegmentOutputWriter(metricOutputOptions
        .asInstanceOf[Map[String, String]], output.segment)
      case OutputType.CSV => new CSVOutputWriter(metricOutputOptions
        .asInstanceOf[Map[String, String]], output.file)
      case OutputType.JSON => new JSONOutputWriter(metricOutputOptions
        .asInstanceOf[Map[String, String]], output.file)
      case OutputType.Parquet => new ParquetOutputWriter(metricOutputOptions
        .asInstanceOf[Map[String, String]], output.file)
      case OutputType.Instrumentation => new InstrumentationOutputWriter(metricOutputOptions
        .asInstanceOf[Map[String, String]], outputConfig.dataFrameName, metricName)
      case OutputType.JDBC => new JDBCOutputWriter(metricOutputOptions
        .asInstanceOf[Map[String, String]], output.jdbc)
      case OutputType.JDBCQuery => new JDBCQueryWriter(metricOutputOptions
        .asInstanceOf[Map[String, String]], output.jdbc)
      case _ => throw new CockatoosException(s"Not Supported Writer $outputType")
    }
    metricOutputWriter.validateMandatoryArguments(metricOutputOptions.asInstanceOf[Map[String, String]])
    metricOutputWriter
  }
}

object OutputType extends Enumeration {
  val Parquet: OutputType.Value = Value("Parquet")
  val Cassandra: OutputType.Value = Value("Cassandra")
  val CSV: OutputType.Value = Value("CSV")
  val JSON: OutputType.Value = Value("JSON")
  val Redshift: OutputType.Value = Value("Redshift")
  //val Redis: OutputType.Value = Value("Redis")
  val Segment: OutputType.Value = Value("Segment")
  val Instrumentation: OutputType.Value = Value("Instrumentation")
  val JDBC: OutputType.Value = Value("JDBC")
  val JDBCQuery: OutputType.Value = Value("JDBCQuery")
}
