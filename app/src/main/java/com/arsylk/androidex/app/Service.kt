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
            concurrency = 3
            module {
                tag = "module 1"
                action {
                    for (i in 0 until 100) {
                        postProgress(i)
                        delay(100)
                    }
                }
            }
            module {
                tag = "module 2"
                action {
                    delay(200)
                    postProgress(3)
                    delay(4000)
                }
            }
            module {
                tag = "module 3"
                action {
                    for (i in 0 until 100) {
                        postProgress(i)
                        delay(200)
                    }
                    delay(4000)
                }
            }
            module {
                tag = "module 4"
                action {
                    postProgress(32, "test")
                    delay(2000)
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