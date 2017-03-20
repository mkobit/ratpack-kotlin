package com.mkobit.ratpack.test

import ratpack.func.Action
import ratpack.handling.Handler
import ratpack.test.handling.HandlingResult
import ratpack.test.handling.RequestFixture

interface KotlinRequestFixture : RequestFixture {

  private class DefaultKotlinRequestFixture(
      val requestFixture: RequestFixture
  ) : KotlinRequestFixture, RequestFixture by requestFixture

  companion object {
    operator fun invoke(
        handler: Handler,
        action: KotlinRequestFixture.() -> Unit
    ): HandlingResult = RequestFixture.handle(handler, Action { fixture ->
      val kotlinFixture = DefaultKotlinRequestFixture(fixture)
      kotlinFixture.action()
    })
  }
}
