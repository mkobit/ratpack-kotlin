package com.mkobit.ratpack.guice

import com.mkobit.ratpack.KotlinRatpackServerSpec
import ratpack.guice.BindingsSpec
import ratpack.guice.Guice

fun KotlinRatpackServerSpec.guiceRegistry(
    callback: BindingsSpec.() -> Unit
): KotlinRatpackServerSpec {
  serverSpecDelegate.registry(Guice.registry { bindings: BindingsSpec ->
    bindings.callback()
  })
  return this
}
