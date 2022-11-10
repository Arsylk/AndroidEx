package com.arsylk.androidex.lib.domain.requester

import kotlinx.coroutines.CompletableDeferred
import kotlin.coroutines.cancellation.CancellationException


internal class RequestImpl<T>(private val deferred: CompletableDeferred<T>) : Request<T> {
    override val isActive get() = deferred.isActive

    override fun complete(value: T) = deferred.complete(value)
    override fun cancel(throwable: Throwable?) = deferred.cancel(CancellationException(throwable))
    override suspend fun await() = deferred.await()
}