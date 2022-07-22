package com.arsylk.androidex.lib.domain.coroutines

import com.arsylk.androidex.lib.domain.holding.HasBag
import com.arsylk.androidex.lib.domain.holding.setTagIfAbsentCompute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

private const val BAG_LOADING_MUTABLE_KEY = "com.arsylk.androidex.lib.domain.coroutines.LoadingEx.loadingTagsMutable"
private const val BAG_LOADING_KEY = "com.arsylk.androidex.lib.domain.coroutines.LoadingEx.loadingTags"
const val LOADING_TAG = "default-loading-tag"

interface LoadingEx : HasBag

private val LoadingEx.loadingTagsMutable: MutableStateFlow<Map<String, Int>>
    get() = bag.setTagIfAbsentCompute(BAG_LOADING_MUTABLE_KEY) { MutableStateFlow(emptyMap()) }

val LoadingEx.loadingTags: StateFlow<Map<String, Int>>
    get() = bag.setTagIfAbsentCompute(BAG_LOADING_KEY) { loadingTagsMutable.asStateFlow() }

val LoadingEx.isLoading: Flow<Boolean>
    get() = loadingTagsMutable.map { it.isNotEmpty() }

fun LoadingEx.isTagLoading(tag: String): Boolean = loadingTagsMutable.value.containsKey(tag)

fun LoadingEx.setTagLoading(tag: String, isLoading: Boolean) {
    val tags = loadingTagsMutable
    when (isLoading) {
        true -> tags.update { map ->
            val current = map[tag] ?: 0
            map + (tag to current + 1)
        }
        false -> tags.update { map ->
            val current = map[tag]
            if (current != null && current > 1) map + (tag to current - 1)
            else map - (tag)
        }
    }
}

inline fun <T> LoadingEx.withLoading(
    tag: String = LOADING_TAG,
    block: () -> T,
): T {
    setTagLoading(tag, isLoading = true)
    return try {
        block()
    } finally {
        setTagLoading(tag, isLoading = false)
    }
}

fun LoadingEx.launchWithLoading(
    context: CoroutineContext = Dispatchers.IO,
    tag: String = LOADING_TAG,
    block: suspend CoroutineScope.() -> Unit,
): Job {
    setTagLoading(tag, isLoading = true)

    val job = bag.scope.launch(context, block = block)
    job.invokeOnCompletion { setTagLoading(tag, isLoading = false) }

    return job
}