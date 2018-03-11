package com.datametica.cockatoos.exceptions

case class CockatoosException(private val message: String = "",
                              private val cause: Throwable = None.orNull)
  extends Exception(message, cause)
