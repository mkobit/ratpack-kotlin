package com.mkobit.ratpack.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode

@DslMarker
annotation class JacksonJsonDsl

fun ObjectMapper.newObjectNode(configuration: ObjectNodeBuilder.() -> Unit): ObjectNode {
  val objectNode: ObjectNode = this.createObjectNode()
  val builder: ObjectNodeBuilder = ObjectNodeBuilder(this.nodeFactory, objectNode)
  builder.configuration()
  return builder.rawNode
}

@JacksonJsonDsl
class ObjectNodeBuilder(
    private val jsonNodeFactory: JsonNodeFactory,
    val rawNode: ObjectNode
) {
  infix fun String.to(value: Int) {
    rawNode.put(this, value)
  }

  infix fun String.to(value: String) {
    rawNode.put(this, value)
  }

  infix fun String.to(value: Boolean) {
    rawNode.put(this, value)
  }

  infix fun String.to(value: Float) {
    rawNode.put(this, value)
  }

  infix fun String.to(value: Double) {
    rawNode.put(this, value)
  }

  infix fun String.to(objectNode: ObjectNode) {
    rawNode.set(this, objectNode)
  }

  fun String.toNull() {
    rawNode.set(this, null)
  }

  fun objectNode(configuration: ObjectNodeBuilder.() -> Unit): ObjectNode {
    val innerBuilder: ObjectNodeBuilder = ObjectNodeBuilder(jsonNodeFactory, jsonNodeFactory.objectNode())
    innerBuilder.configuration()
    return innerBuilder.rawNode
  }
}
