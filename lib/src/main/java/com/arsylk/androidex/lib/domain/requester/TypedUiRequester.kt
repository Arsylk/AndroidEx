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

class TypedUiRequester<Data: Any, Type: Any> {
    private val mutex = Mutex()
    private val shared = MutableSharedFlow<Pair<Request<Type>, Data>?>(
        replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val pending: Pair<Request<Type>, Data>? get() {
        val (request, data) = shared.replayCache.firstOrNull() ?: return null
        return if (request.isActive) request to data else null
    }

    suspend fun receive(block: suspend (Request<Type>, Data) -> Unit) {
        coroutineScope {
            var previous: Job? = null
            shared.collect { pair ->
                previous?.cancelAndJoin()
                if (pair != null) {
                    val (request, data) = pair
                    if (request.isActive) {
                        previous = launch(start = CoroutineStart.UNDISPATCHED) {
                            block.invoke(request, data)
                        }
                    }
                }
            }
        }
    }

    suspend fun request(data: Data): Type {
        return mutex.withLock {
            coroutineScope {
                val request = RequestImpl(CompletableDeferred<Type>(coroutineContext.job))
                val pair = request to data
                launch { shared.emit(pair) }

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
