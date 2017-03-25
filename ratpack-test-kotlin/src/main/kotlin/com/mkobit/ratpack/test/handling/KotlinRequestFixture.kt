package com.mkobit.ratpack.test.handling

import ratpack.func.Action
import ratpack.handling.Handler
import ratpack.registry.RegistrySpec
import ratpack.server.ServerConfigBuilder
import ratpack.test.handling.HandlingResult
import ratpack.test.handling.RequestFixture

class KotlinRequestFixture private constructor(
    val requestFixture: RequestFixture
) : RequestFixture by requestFixture {

  fun registry(spec: RegistrySpec.() -> Unit): KotlinRequestFixture = apply {
    requestFixture.registry {
      it.spec()
    }
  }

  fun serverConfig(config: ServerConfigBuilder.() -> Unit): KotlinRequestFixture = apply {
    requestFixture.serverConfig {
      it.config()
    }
  }

  companion object {
    operator fun invoke(
        handler: Handler,
        action: KotlinRequestFixture.() -> Unit
    ): HandlingResult = RequestFixture.handle(handler, Action { fixture ->
      val kotlinFixture = KotlinRequestFixture(fixture)
      kotlinFixture.action()
    })
  }
}
