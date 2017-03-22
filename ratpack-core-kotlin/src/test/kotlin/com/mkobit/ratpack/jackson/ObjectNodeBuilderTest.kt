package com.mkobit.ratpack.jackson

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ObjectNodeBuilderTest {

  private val nodeFactory: JsonNodeFactory = JsonNodeFactory.instance
  private lateinit var rawNode: ObjectNode
  private lateinit var builder: ObjectNodeBuilder

  @BeforeEach
  internal fun setUp() {
    rawNode = nodeFactory.objectNode()
    builder = ObjectNodeBuilder(nodeFactory, rawNode)
  }

  @Test
  internal fun `node is initially empty`() {
    assertThat(builder.rawNode.size()).isEqualTo(0)
  }

  @Test
  internal fun `can set a value to a string`() {
    builder.apply {
      "a" to "b"
    }

    builder.rawNode.let {
      assertThat(it.size()).isEqualTo(1)
      assertThat(it["a"].isTextual).isTrue()
      assertThat(it["a"].asText()).isEqualTo("b")
    }
  }

  @Test
  internal fun `can set a value to an int`() {
    builder.apply {
      "a" to Int.MAX_VALUE
    }

    builder.rawNode.let {
      assertThat(it.size()).isEqualTo(1)
      assertThat(it["a"].isInt).isTrue()
      assertThat(it["a"].asInt()).isEqualTo(Int.MAX_VALUE)
    }
  }

  @Test
  internal fun `can set a value to a boolean`() {
    builder.apply {
      "a" to true
    }

    builder.rawNode.let {
      assertThat(it.size()).isEqualTo(1)
      assertThat(it["a"].isBoolean).isTrue()
      assertThat(it["a"].asBoolean()).isTrue()
    }
  }

  @Test
  internal fun `can set a value to an object`() {
    val expectedNode: ObjectNode = nodeFactory.objectNode()

    builder.apply {
      "a" to expectedNode
    }

    builder.rawNode.let {
      assertThat(it.size()).isEqualTo(1)
      assertThat(it["a"]).isEqualTo(expectedNode)
    }
  }

  @Test
  internal fun `can set a value to null`() {
    builder.apply {
      "a".toNull()
    }

    builder.rawNode.let {
      assertThat(it.size()).isEqualTo(1)
      assertThat(it["a"].isNull).isTrue()
    }
  }

  @Test
  internal fun `can set a value to a double`() {
    builder.apply {
      "a" to Double.MAX_VALUE
    }

    builder.rawNode.let {
      assertThat(it.size()).isEqualTo(1)
      assertThat(it["a"].isDouble).isTrue()
      assertThat(it["a"].asDouble()).isEqualTo(Double.MAX_VALUE)
    }
  }

  @Test
  internal fun `can set a value to a float`() {
    builder.apply {
      "a" to Float.MAX_VALUE
    }

    builder.rawNode.let {
      assertThat(it.size()).isEqualTo(1)
      assertThat(it["a"].isFloat).isTrue()
      assertThat(it["a"].floatValue()).isEqualTo(Float.MAX_VALUE)
    }
  }

  @Test
  internal fun `can set multiple different values`() {
    builder.apply {
      "a" to "value"
      "b" to 0
    }

    builder.rawNode.let {
      assertThat(it.size()).isEqualTo(2)
      assertThat(it["a"].isTextual).isTrue()
      assertThat(it["a"].asText()).isEqualTo("value")
      assertThat(it["b"].asInt()).isEqualTo(0)
      assertThat(it["b"].isInt).isTrue()
    }
  }

  @Test
  internal fun `can overwrite a value`() {
    builder.apply {
      "a" to "value"
      "a" to "newValue"
    }

    builder.rawNode.let {
      assertThat(it.size()).isEqualTo(1)
      assertThat(it["a"].isTextual).isTrue()
      assertThat(it["a"].asText()).isEqualTo("newValue")
    }
  }

  @Test
  internal fun `can create nested object nodes`() {
    builder.apply {
      "a" to objectNode {
        "b" to objectNode {
          "c" to "d"
        }
      }
    }
    builder.rawNode.let {
      assertThat(it.size()).isEqualTo(1)
      assertThat(it["a"].isObject).isTrue()
      it["a"].let {
        assertThat(it.size()).isEqualTo(1)
        it["b"].let {
          assertThat(it.size()).isEqualTo(1)
          assertThat(it["c"].isTextual).isTrue()
          assertThat(it["c"].asText()).isEqualTo("d")
        }
      }
    }
  }
}
