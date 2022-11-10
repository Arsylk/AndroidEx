package com.arsylk.androidex.lib.domain.requester

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class UiRequester<Base: UiRequest<out Any>> {
    private val mutex = Mutex()
    private val shared = MutableSharedFlow<Base?>(
        replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val pending: Base? get() {
        val request = shared.replayCache.firstOrNull() ?: return null
        return if (request.request.isActive) request else null
    }

    suspend fun receive(block: suspend (Base) -> Unit) {
        coroutineScope {
            var previous: Job? = null
            shared.collect { ui ->
                previous?.cancelAndJoin()
                if (ui?.request?.isActive == true) {
                    previous = launch(start = CoroutineStart.UNDISPATCHED) {
                        block.invoke(ui)
                    }
                }
            }
        }
    }

    suspend fun <Value> request(block: (Request<Value>) -> Base): Value {
        return mutex.withLock {
            coroutineScope {
                val request = RequestImpl(CompletableDeferred<Value>(coroutineContext.job))
                val uiRequest = block.invoke(request)
                launch { shared.emit(uiRequest) }

                try {
                    request.await()
                } finally {
                    withContext(NonCancellable) {
                        shared.emit(null)
                    }
                }
            }
        }
    }
}