package com.datametica.cockatoos.exceptions

case class CockatoosInvalidMetricFileException(private val message: String = "",
                                        private val cause: Throwable = None.orNull)
  extends Exception(message, cause)
