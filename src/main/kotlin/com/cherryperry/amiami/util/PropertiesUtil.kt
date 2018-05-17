package com.cherryperry.amiami.util

import com.cherryperry.amiami.App
import java.util.* // ktlint-disable no-wildcard-imports

fun <T> readProperty(file: String, block: Properties.() -> T): T {
    return App::class.java.classLoader.getResourceAsStream(file).use {
        val properties = Properties()
        properties.load(it)
        block(properties)
    }
}