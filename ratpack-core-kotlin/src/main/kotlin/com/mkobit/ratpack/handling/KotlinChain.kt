package com.mkobit.ratpack.handling

import ratpack.handling.Chain
import ratpack.handling.Context

class KotlinChain(val delegate: Chain) : Chain by delegate {

  fun all(action: Context.() -> Unit): KotlinChain = apply {
    delegate.all { it.action() }
  }

  fun delete(action: Context.() -> Unit): KotlinChain = apply {
    delegate.delete(action)
  }

  fun delete(path: String, action: Context.() -> Unit): KotlinChain = apply {
    delegate.delete(path) { it.action() }
  }

  fun get(action: Context.() -> Unit): KotlinChain = apply {
    delegate.get(action)
  }

  fun get(path: String, action: Context.() -> Unit): KotlinChain = apply {
    delegate.get(path) { it.action() }
  }

  fun post(path: String, action: Context.() -> Unit): KotlinChain = apply {
    delegate.post(path) { it.action() }
  }

  fun post(action: Context.() -> Unit): KotlinChain = apply {
    delegate.post(action)
  }

  fun put(action: Context.() -> Unit): KotlinChain = apply {
    delegate.put(action)
  }

  fun put(path: String, action: Context.() -> Unit): KotlinChain = apply {
    delegate.put(path) { it.action() }
  }
}
