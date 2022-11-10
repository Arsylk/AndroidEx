package com.arsylk.androidex.lib.domain.sync

import com.arsylk.androidex.lib.domain.sync.component.ISyncGroup
import com.arsylk.androidex.lib.domain.sync.component.ISyncModule
import com.arsylk.androidex.lib.domain.sync.component.SyncComponent
import com.arsylk.androidex.lib.domain.sync.model.SyncResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import java.util.concurrent.ConcurrentHashMap

data class ComponentResult(
    val component: SyncComponent,
    val result: SyncResult,
    val weight: Float,
)

data class ProgressUpdate(
    val result: ComponentResult,
    val undelivered: Map<SyncComponent, ComponentResult>,
)

data class State(
    val isActive: Boolean,
    val result: SyncResult?,
)

class SyncStateStore {
    private val _isActive = MutableStateFlow(false)
    private val _result = MutableStateFlow<SyncResult?>(null)
    private val map = ConcurrentHashMap<SyncComponent, ComponentResult>()
    private val undelivered = ConcurrentHashMap<SyncComponent, ComponentResult>()
    private val messages = mutableListOf<String>()
    val state = combine(_isActive, _result) { isActive, result ->
        State(isActive = isActive, result = result)
    }
    private val _onProgress = Channel<ProgressUpdate>()
    val onProgress: ReceiveChannel<ProgressUpdate> = _onProgress

    fun reset() {
        _isActive.value = false
        _result.value = null
        map.clear()
        messages.clear()
        undelivered.clear()
    }

    fun setResult(result: SyncResult) {
        _result.value = result
    }

    fun setProgress(componentResult: ComponentResult) {
        val component = componentResult.component
        map[component] = componentResult
        trySend(componentResult)

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

            var didPut = false
            val next = map.getOrPut(component) {
                didPut = true
                ComponentResult(component, fillerResult, componentWeight)
            }
            if (didPut) trySend(next)

            if (component is ISyncGroup)
                setGroupProgress(component, fillerResult, componentWeight)
        }
    }

    private fun trySend(result: ComponentResult) {
        undelivered.remove(result.component)
        val didSend = _onProgress.trySend(
            ProgressUpdate(result = result, undelivered = undelivered.toMap())
        ).also { println("didSend: $it") }
        if (didSend.isSuccess) {
            undelivered.clear()
        } else {
            undelivered[result.component] = result
        }
    }

    fun getProgress(): Float {
        return map.filter { (k, _) -> k is ISyncModule }
            .values
            .sumOf { it.weight * it.result.percentage }
    }

    fun iterateMap(onEach: (SyncComponent, ComponentResult) -> Unit) {
        val current = map.toMap()
        current.forEach { (k, v) -> onEach(k, v) }
    }

    operator fun get(key: SyncComponent) = map[key]
}