package com.mkobit.ratpack.server

import com.mkobit.ratpack.handling.KotlinChain
import ratpack.server.RatpackServerSpec
import ratpack.server.ServerConfigBuilder

/**
 * Wrapper class that delegates to the [RatpackServerSpec].
 * @param delegate the server spec delegate
 */
class KotlinRatpackServerSpec(val delegate: RatpackServerSpec): RatpackServerSpec by delegate {
  /**
   * Set the configuration of the server.
   */
  fun serverConfig(action: ServerConfigBuilder.() -> Unit): KotlinRatpackServerSpec = apply {
    delegate.serverConfig {
      it.action()
    }
  }

  fun handlers(action: KotlinChain.() -> Unit): KotlinRatpackServerSpec = apply {
    delegate.handlers {
      KotlinChain(it).action()
    }
  }
}
