package com.arsylk.androidex.lib.domain.coroutines

import com.arsylk.androidex.lib.domain.holding.HasBag
import com.arsylk.androidex.lib.domain.holding.setTagIfAbsentCompute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentSkipListSet
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext

private const val BAG_DEBOUNCE_KEY = "com.arsylk.androidex.lib.domain.coroutines.DebounceEx.debounceTags"
const val DEBOUNCE_TAG = "default-loading-tag"

interface DebounceEx : HasBag

val DebounceEx.debounceTags: ConcurrentSkipListSet<String>
    get() = bag.setTagIfAbsentCompute(BAG_DEBOUNCE_KEY) { ConcurrentSkipListSet() }

@OptIn(ExperimentalContracts::class)
inline fun DebounceEx.withDebounce(
    tag: String = DEBOUNCE_TAG,
    block: () -> Unit,
) {
    contract { callsInPlace(block, kind = InvocationKind.AT_MOST_ONCE) }
    val tags = debounceTags
    if (tag in tags) return
    tags += tag
    try {
        block()
    } finally {
        tags -= tag
    }
}

@OptIn(ExperimentalContracts::class)
fun DebounceEx.launchWithDebounce(
    context: CoroutineContext = Dispatchers.IO,
    tag: String = DEBOUNCE_TAG,
    block: suspend CoroutineScope.() -> Unit,
): Job? {
    contract { callsInPlace(block, kind = InvocationKind.AT_MOST_ONCE) }
    val tags = debounceTags
    if (tag in tags) return null

    val job = bag.scope.launch(context, block = block)
    job.invokeOnCompletion { tags -= tag }

    return job
}