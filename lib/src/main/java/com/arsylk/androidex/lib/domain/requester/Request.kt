package com.arsylk.androidex.lib.domain.requester

import kotlinx.coroutines.CompletableDeferred
import kotlin.coroutines.cancellation.CancellationException

interface Request<T> {
    val isActive: Boolean

    fun complete(value: T): Boolean
    fun cancel(throwable: Throwable? = null)
    suspend fun await(): T
}