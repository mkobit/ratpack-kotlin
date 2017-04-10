package com.mkobit.ratpack.handling

import com.nhaarman.mockito_kotlin.KArgumentCaptor
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ratpack.handling.Chain
import ratpack.handling.Context
import ratpack.handling.Handler

internal class KotlinChainTest {

  lateinit var mockChain: Chain
  lateinit var mockContext: Context
  lateinit var chain: KotlinChain
  lateinit var handlerCaptor: KArgumentCaptor<Handler>

  @BeforeEach
  internal fun setUp() {
    mockChain = mock()
    mockContext = mock()
    handlerCaptor = argumentCaptor()
    chain = KotlinChain(mockChain)
  }

  @Test
  internal fun all() {
    chain.all {
      render("hello")
    }
    verify(mockChain).all(handlerCaptor.capture())
    handlerCaptor.firstValue.handle(mockContext)
    verify(mockContext).render("hello")
  }

  @Test
  internal fun `delete`() {
    chain.delete {
      render("hello")
    }

    verify(mockChain).delete(handlerCaptor.capture())
    handlerCaptor.firstValue.handle(mockContext)
    verify(mockContext).render("hello")
  }

  @Test
  internal fun `delete with path`() {
    chain.delete("path") {
      render("hello")
    }

    verify(mockChain).delete(eq("path"), handlerCaptor.capture())
    handlerCaptor.firstValue.handle(mockContext)
    verify(mockContext).render("hello")
  }

  @Test
  internal fun `get`() {
    chain.get {
      render("hello")
    }

    verify(mockChain).get(handlerCaptor.capture())
    handlerCaptor.firstValue.handle(mockContext)
    verify(mockContext).render("hello")
  }

  @Test
  internal fun `get with path`() {
    chain.get("path") {
      render("hello")
    }

    verify(mockChain).get(eq("path"), handlerCaptor.capture())
    handlerCaptor.firstValue.handle(mockContext)
    verify(mockContext).render("hello")
  }

  @Test
  internal fun `post`() {
    chain.post {
      render("hello")
    }

    verify(mockChain).post(handlerCaptor.capture())
    handlerCaptor.firstValue.handle(mockContext)
    verify(mockContext).render("hello")
  }

  @Test
  internal fun `post with path`() {
    chain.post("path") {
      render("hello")
    }

    verify(mockChain).post(eq("path"), handlerCaptor.capture())
    handlerCaptor.firstValue.handle(mockContext)
    verify(mockContext).render("hello")
  }

  @Test
  internal fun `put`() {
    chain.put {
      render("hello")
    }

    verify(mockChain).put(handlerCaptor.capture())
    handlerCaptor.firstValue.handle(mockContext)
    verify(mockContext).render("hello")
  }

  @Test
  internal fun `put with path`() {
    chain.put("path") {
      render("hello")
    }

    verify(mockChain).put(eq("path"), handlerCaptor.capture())
    handlerCaptor.firstValue.handle(mockContext)
    verify(mockContext).render("hello")
  }
}
