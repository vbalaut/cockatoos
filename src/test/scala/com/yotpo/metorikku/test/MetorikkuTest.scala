package com.datametica.cockatoos.test

import java.io.{File, FileNotFoundException}

import com.datametica.cockatoos.exceptions.CockatoosInvalidMetricFileException
import com.datametica.cockatoos.runners.Cockatoos
import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfterAll, FunSuite}

class CockatoosTest extends FunSuite with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    org.apache.commons.io.FileUtils.deleteDirectory(new File("src/test/out"))
  }

  test("Test Cockatoos should load a table and filter") {
    val sparkSession = SparkSession.builder.getOrCreate()

    Cockatoos.main(Array(
      "-c", "src/test/scala/com.datametica.cockatoos/test/cockatoos-test-config.yaml"))

    assert(new File("src/test/out/metric_test/metric/testOutput/._SUCCESS.crc").exists)
    assert(new File("src/test/out/metric_test/metric/filteredOutput/._SUCCESS.crc").exists)

    val testOutput = sparkSession.table("testOutput")
    val filterOutput = sparkSession.table("filteredOutput")

    testOutput.cache
    filterOutput.cache

    assert(testOutput.count === 5)
    assert(filterOutput.count === 1)
  }

  test("Test Cockatoos should Fail on invalid metics") {
    val thrown = intercept[FileNotFoundException] {
      Cockatoos.main(Array("-c", "src/test/scala/com.datametica.cockatoos/test/cockatoos-test-config-invalid-metrics.yaml"))
    }
    assert(thrown.getMessage.startsWith("No Files to Run"))

  }

  test("Test Cockatoos should Fail on invalid inputs path") {
    val thrown = intercept[Exception] {
      Cockatoos.main(Array("-c", "src/test/scala/com.datametica.cockatoos/test/cockatoos-test-config-invalid-inputs.yaml"))
    }
    assert(thrown.getMessage.startsWith("Path does not exist"))

  }

  test("Test Cockatoos should Fail on invalid Writer") {
    assertThrows[CockatoosInvalidMetricFileException] {
      Cockatoos.main(Array("-c", "src/test/scala/com.datametica.cockatoos/test/cockatoos-test-config-invalid-writer.yaml"))
    }
  }

  //TODO(etrabelsi@yotpo.com) add Test Cockatoos should Fail on invalid Writer query fail gracefully

  test("Test Cockatoos should Fail on invalid query without fail non gracefully") {
    val thrown = intercept[Exception] {
      Cockatoos.main(Array("-c", "src/test/scala/com.datametica.cockatoos/test/cockatoos-test-config-invalid-query.yaml"))
    }
    assert(thrown.getCause.getMessage.startsWith("cannot resolve '`non_existing_column`'"))
  }
}
