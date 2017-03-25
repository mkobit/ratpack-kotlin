package com.mkobit.ratpack.server

import ratpack.server.RatpackServer

/**
 * Starts a [RatpackServer] from the provided configuration.
 * @see [RatpackServer.start]
 */
fun serverStart(action: KotlinRatpackServerSpec.() -> Unit): RatpackServer = RatpackServer.start {
  KotlinRatpackServerSpec(it).action()
}

/**
 * Creates a [RatpackServer] from the provided configuration.
 * @see [RatpackServer.of]
 */
fun serverOf(action: KotlinRatpackServerSpec.() -> Unit): RatpackServer = RatpackServer.of {
  KotlinRatpackServerSpec(it).action()
}
