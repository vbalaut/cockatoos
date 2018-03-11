package com.datametica.cockatoos.calculators

import org.apache.spark.sql.DataFrame

trait Calculator {
  def calculate(): DataFrame
}
