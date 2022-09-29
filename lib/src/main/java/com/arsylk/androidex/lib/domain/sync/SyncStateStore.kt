package com.arsylk.androidex.lib.domain.sync

import com.arsylk.androidex.lib.domain.sync.component.ISyncGroup
import com.arsylk.androidex.lib.domain.sync.component.ISyncModule
import com.arsylk.androidex.lib.domain.sync.component.SyncComponent
import com.arsylk.androidex.lib.domain.sync.model.SyncResult
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import java.util.concurrent.ConcurrentHashMap

data class ComponentResult(
    val component: SyncComponent,
    val result: SyncResult,
    val weight: Float,
)

data class SimpleProgress(
    val result: SyncResult,
    val weight: Float,
)

data class State(
    val isActive: Boolean,
    val result: SyncResult?,
)

class SyncStateStore {
    private val _isActive = MutableStateFlow(false)
    private val _result = MutableStateFlow<SyncResult?>(null)
    private val map = ConcurrentHashMap<SyncComponent, SimpleProgress>()
    private val messages = mutableListOf<String>()
    val state = combine(_isActive, _result) { isActive, result ->
        State(isActive = isActive, result = result)
    }
    val onProgress = Channel<Pair<SyncComponent, SimpleProgress>>(capacity = 64, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    fun reset() {
        _isActive.value = false
        _result.value = null
        map.clear()
        messages.clear()
    }

    fun setResult(result: SyncResult) {
        _result.value = result
    }

    fun setProgress(componentResult: ComponentResult) {
        val component = componentResult.component
        val next = SimpleProgress(componentResult.result, componentResult.weight)
        map[component] = next
        onProgress.trySend(component to next)

        when (component) {
            is ISyncModule -> {
                val progress = componentResult.result as? SyncResult.Progress
                if (progress?.message != null) {
                    synchronized(messages) {
                        messages += progress.message
                    }
                }
            }
            is ISyncGroup -> {
                setGroupProgress(component, componentResult.result, componentResult.weight)
            }
        }
    }

    private fun setGroupProgress(group: ISyncGroup, result: SyncResult, weight: Float) {
        val fillerResult = when (result) {
            is SyncResult.Success -> SyncResult.Success(skipped = true)
            is SyncResult.Error -> SyncResult.Error(result.throwable)
            else -> return
        }
        group.components.forEach { component ->
            val weightSum = group.components.sumOf { it.weight }
            val componentWeight = (component.weight / weightSum) * weight

            map.getOrPut(component) {
                val next = SimpleProgress(fillerResult, componentWeight)
                onProgress.trySend(component to next)
                next
            }

            if (component is ISyncGroup)
                setGroupProgress(component, fillerResult, componentWeight)
        }
    }

    fun getProgress(): Float {
        return map.filter { (k, _) -> k is ISyncModule }
            .values
            .sumOf { it.weight * it.result.percentage }
    }

    fun iterateMap(onEach: (SyncComponent, SimpleProgress) -> Unit) {
        val current = map.toMap()
        current.forEach { (k, v) -> onEach(k, v) }
    }

    operator fun get(key: SyncComponent) = map[key]
}