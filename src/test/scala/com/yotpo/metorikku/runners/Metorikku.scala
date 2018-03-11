package com.datametica.cockatoos.runners

// This class is used to allow metric runner in IDEs (loading all provided in compile time)
object Cockatoos {
  def main(args: Array[String]): Unit = {
    com.datametica.cockatoos.Cockatoos.main(args)
  }
}
