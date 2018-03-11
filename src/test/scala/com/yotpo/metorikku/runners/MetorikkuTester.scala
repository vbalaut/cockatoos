package com.datametica.cockatoos.runners

// This class is used to allow metric runner in IDEs (loading all provided in compile time)
object CockatoosTester {
  def main(args: Array[String]): Unit = {
    com.datametica.cockatoos.CockatoosTester.main(args)
  }
}
