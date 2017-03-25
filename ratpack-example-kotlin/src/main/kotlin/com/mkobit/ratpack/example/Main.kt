package com.mkobit.ratpack.example

import com.mkobit.ratpack.server.serverStart
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Main {

  private val logger: Logger = LoggerFactory.getLogger(Main::class.java)

  @JvmStatic
  fun main(args: Array<String>) = serverStart {
    handlers {
    }
  }
}
