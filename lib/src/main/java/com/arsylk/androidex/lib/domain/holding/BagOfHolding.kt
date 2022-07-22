package com.arsylk.androidex.lib.domain.holding

import java.io.Closeable

interface BagOfHolding : Closeable {
    fun <T> getTag(key: String): T
    fun <T> setTagIfAbsent(key: String, value: T): T
}