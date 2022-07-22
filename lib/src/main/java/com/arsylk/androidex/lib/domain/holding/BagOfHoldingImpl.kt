package com.arsylk.androidex.lib.domain.holding

import java.io.Closeable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

internal class BagOfHoldingImpl : BagOfHolding {
    private var isClosed = false
    private val map = hashMapOf<String, Any?>()

    @Suppress("UNCHECKED_CAST")
    override fun <T> getTag(key: String): T {
        return synchronized(map) {
            map[key] as T
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> setTagIfAbsent(key: String, value: T): T {
        val previous: T?
        synchronized(map) {
            previous = map[key] as T?
            if (previous == null) map[key] = value
        }
        val current = previous ?: value
        if (isClosed) closeObject(current)

        return current
    }

    override fun close() {
        isClosed = true
        synchronized(map) {
            for (value in map.values) {
                closeObject(value)
            }
        }
    }

    private fun closeObject(any: Any?) {
        if (any is Closeable) {
            any.close()
        }
    }
}