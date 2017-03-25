package com.mkobit.ratpack.handling

import ratpack.handling.Chain
import ratpack.handling.Handler

class KotlinChain(val delegate: Chain) : Chain by delegate {

  fun get(path: String, handler: Handler.() -> Unit) {
    delegate.get(path) {
    }
  }
}
