package com.mkobit.ratpack.handling

import ratpack.handling.Handler

class KotlinHandler(val delegate: Handler) : Handler by delegate {
}
