package com.datametica.cockatoos.configuration

import java.nio.file.{Files, Paths}

import scopt.OptionParser

/**
  * Created by vbalaut on 09/03/18.
  */
object CLIConfigurationParser {

  val parser: OptionParser[ConfigFileName] = new scopt.OptionParser[ConfigFileName]("Cockatoos") {
    head("Cockatoos", "1.0")
    opt[String]('c', "config")
      .text("The YAML file that defines the Cockatoos arguments")
      .action((x, c) => c.copy(filename = x))
      .validate(x => {
        if (Files.exists(Paths.get(x))) {
          success
        }
        else {
          failure("Supplied YAML file not found")
        }
      }).required()
    help("help") text "use command line arguments to specify the YAML configuration file path"
  }

  case class ConfigFileName(filename: String = "")

}
