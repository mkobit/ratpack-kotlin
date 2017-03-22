package com.mkobit.ratpack

import ratpack.server.RatpackServer
import ratpack.server.RatpackServerSpec
import ratpack.server.ServerConfigBuilder

/**
 * Starts a [RatpackServer] with the provided configuration.
 * @see [RatpackServer.start]
 */
fun startServer(callback: KotlinRatpackServerSpec.() -> Unit): RatpackServer = RatpackServer.start {
  KotlinRatpackServerSpec(it).callback()
}

/**
 * Wrapper class that delegates to the [RatpackServerSpec].
 * @param serverSpecDelegate the server spec delegate
 */
class KotlinRatpackServerSpec(val serverSpecDelegate: RatpackServerSpec): RatpackServerSpec by serverSpecDelegate {

  /**
   * Set the configuration of the server.
   */
  fun serverConfig(callback: ServerConfigBuilder.() -> Unit): RatpackServerSpec {

    return serverSpecDelegate.serverConfig {
      it.callback()
    }
  }
}
