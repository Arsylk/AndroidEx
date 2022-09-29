package com.arsylk.androidex.app

import com.arsylk.androidex.lib.domain.sync.SyncExecutor
import com.arsylk.androidex.lib.domain.sync.sync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class Service {
    val scope = CoroutineScope(Job())
    var cyclicJob: Job? = null
    val cyclic = SyncExecutor()
    val root = sync {
        group {
            tag = "nya"
            concurrency = 2
            module {
                tag = "module 1"
                action {
                    postProgress(32)
                    delay(2000)
                    postProgress(94)
                    delay(1000)
                    store += cyclic
                }
            }
            module {
                tag = "module 2"
                action { delay(4000) }
            }
        }
        group {
            tag = "group 2"
            required = true
            group {
                tag = "inner group 1"
                module {
                    action { throw IllegalStateException("hi") }
                }
            }
            group {
                tag = "inner group 2"
                module {
                    tag = "await"
                    action {
                        postProgress(1)
                        println("started awaiting")
                        val executor: SyncExecutor = store.awaitForever()
                        println("stopped: $executor")
                        postProgress(99)
                        delay(2000)
                    }
                }
            }
            group {
                tag = "concurrent"
                concurrency = 5
                module {
                    tag = "send 1"
                    action {
                        delay(1000)
                        store["1"] = "123"
                    }
                }
                module {
                    tag = "send 2"
                    action {
                        delay(3000)
                        store["2"] = 5239
                    }
                }
                module {
                    tag = "receive 1"
                    action {
                        postProgress(1)
                        val got = store.awaitForever<String>("1")
                        postProgress(99)
                        delay(500)
                    }
                }
                module {
                    tag = "receive 2"
                    action {
                        postProgress(1)
                        val got = store.awaitForever<Int>("2")
                        postProgress(99)
                        delay(500)
                    }
                }
            }
        }
    }

    fun getOrStart() = cyclic.getOrStart(root)

    fun startCyclic(interval: Duration = 4.seconds) {
        cyclicJob = scope.launch {
            var next = interval
            while (isActive) {
                val start = System.currentTimeMillis().milliseconds
                delay(next)
                val sinceLast = cyclic.lastFinish - start
                if (sinceLast > Duration.ZERO) {
                    next = sinceLast
                } else {
                    next = interval
                    cyclic.getOrStart(root).join()
                }
            }
        }
    }
}