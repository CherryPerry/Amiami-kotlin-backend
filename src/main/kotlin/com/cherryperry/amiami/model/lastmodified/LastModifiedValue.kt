package com.cherryperry.amiami.model.lastmodified

import java.util.concurrent.atomic.AtomicLong

class LastModifiedValue {

    private val timestamp = AtomicLong()

    init {
        update()
    }

    val value: Long
        get() = timestamp.get()

    fun update() {
        timestamp.set(System.currentTimeMillis())
    }
}
