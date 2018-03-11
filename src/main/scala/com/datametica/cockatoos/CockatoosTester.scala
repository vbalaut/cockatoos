package com.datametica.cockatoos

import java.nio.file.{Files, Paths}

import com.datametica.cockatoos.TesterConfigurationParser.CockatoosTesterArgs
import com.datametica.cockatoos.session.Session
import com.datametica.cockatoos.utils.TestUtils
import org.apache.log4j.LogManager
import scopt.OptionParser


object CockatoosTester extends App {
  lazy val log = LogManager.getLogger(this.getClass)
  val cockatoosTesterArgs = TesterConfigurationParser.parser.parse(args, CockatoosTesterArgs()).getOrElse(CockatoosTesterArgs())

  cockatoosTesterArgs.settings.foreach(settings => {
    val metricTestSettings = TestUtils.getTestSettings(settings)
    val config = TestUtils.createCockatoosConfigFromTestSettings(settings, metricTestSettings, cockatoosTesterArgs.preview)
    Session.init(config)
    TestUtils.runTests(metricTestSettings.tests)
  })

}

object TesterConfigurationParser {
  val NumberOfPreviewLines = 10

  case class CockatoosTesterArgs(settings: Seq[String] = Seq(), preview: Int = NumberOfPreviewLines)

  val parser: OptionParser[CockatoosTesterArgs] = new scopt.OptionParser[CockatoosTesterArgs]("CockatoosTester") {
    head("CockatoosTesterRunner", "1.0")
    opt[Seq[String]]('t', "test-settings")
      .valueName("<test-setting1>,<test-setting2>...")
      .action((x, c) => c.copy(settings = x))
      .text("test settings for each metric set")
      .validate(x => {
        if (x.exists(f => !Files.exists(Paths.get(f)))) {
          failure("One of the file is not found")
        }
        else {
          success
        }
      })
      .required()
    opt[Int]('p', "preview").action((x, c) =>
      c.copy(preview = x)).text("number of preview lines for each step")
    help("help") text "use command line arguments to specify the settings for each metric set"
  }
}
