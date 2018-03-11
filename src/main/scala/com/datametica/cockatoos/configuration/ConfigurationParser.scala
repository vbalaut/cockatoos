package com.datametica.cockatoos.configuration

import java.io.FileReader

import com.datametica.cockatoos.exceptions.{CockatoosException, CockatoosInvalidMetricFileException}
import com.datametica.cockatoos.utils.FileUtils
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.apache.log4j.{LogManager, Logger}
import com.datametica.cockatoos.configuration.CLIConfigurationParser.ConfigFileName
/**
  * Created by vbalaut on 09/03/18.
  */
object ConfigurationParser {
  val log: Logger = LogManager.getLogger(this.getClass)

  def parse(args: Array[String]): ConfigurationFile = {
    log.info("Starting Cockatoos - Parsing configuration")
    

    CLIConfigurationParser.parser.parse(args, ConfigFileName()) match {
      case Some(arguments) =>
        parseConfigurationFile(arguments.filename)
      case None => throw new CockatoosException("Failed to parse config file")
    }
  }

  def parseConfigurationFile(fileName: String): ConfigurationFile = {
    FileUtils.getObjectMapperByExtension(fileName) match {
      case Some(mapper) => {
        mapper.registerModule(DefaultScalaModule)
        mapper.readValue(new FileReader(fileName), classOf[ConfigurationFile])
      }
      case None => throw CockatoosInvalidMetricFileException(s"Unknown extension for file $fileName")
    }
  }
}
