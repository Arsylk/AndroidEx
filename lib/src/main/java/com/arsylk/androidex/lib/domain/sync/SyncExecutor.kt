package com.arsylk.androidex.lib.domain.sync

import com.arsylk.androidex.lib.domain.sync.component.ISyncGroup
import com.arsylk.androidex.lib.domain.sync.component.ISyncModule
import com.arsylk.androidex.lib.domain.sync.component.SyncComponent
import com.arsylk.androidex.lib.domain.sync.group.RootSyncGroup
import com.arsylk.androidex.lib.domain.sync.group.SyncComponentContext
import com.arsylk.androidex.lib.domain.sync.model.SyncComponentConditionException
import com.arsylk.androidex.lib.domain.sync.model.SyncGroupException
import com.arsylk.androidex.lib.domain.sync.model.SyncModuleException
import com.arsylk.androidex.lib.domain.sync.model.SyncResult
import com.arsylk.androidex.lib.domain.sync.module.SyncModuleContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class SyncExecutor {
    private val scope = CoroutineScope(Job())
    val progress = SyncStateStore()
    private var job: Job? = null
    var lastFinish = Duration.ZERO
        private set

    fun getOrStart(root: RootSyncGroup): Job {
        val current = job
        if (current?.isActive == true) return current

        progress.reset()
        val job = prepareGroupFlow(root.store, root, root.weight)
            .onEach(progress::setProgress)
            .catch { throwable ->
                progress.setProgress(ComponentResult(root, SyncResult.Error(throwable), root.weight))
                throw throwable
            }
            .onCompletion { throwable ->
                val result = if (throwable != null) SyncResult.Error(throwable)
                else SyncResult.Success(skipped = false)
                progress.setResult(result)
                lastFinish = System.currentTimeMillis().milliseconds
            }
            .catch {  }
            .launchIn(scope)
        this.job = job
        return job
    }

    fun cancel() {
        job?.cancel()
    }


    private fun prepareGroupFlow(
        store: SyncGroupStore,
        group: ISyncGroup,
        weight: Float,
    ): Flow<ComponentResult> {
        val componentList = group.components.sortedBy(SyncComponent::order)
        val weightSum = componentList.sumOf(SyncComponent::weight)

        return componentList.asFlow()
            .flatMapMerge(group.concurrency) { component ->
                val componentWeight = weight * (component.weight / weightSum)
                val componentSkip = component
                    .runCatching { !condition.invoke(SyncComponentContext(store)) }
                    .getOrElse { t -> throw SyncComponentConditionException(component, t) }

                when {
                    componentSkip -> {
                        flowOf(ComponentResult(component, SyncResult.Success(skipped = true), componentWeight))
                    }
                    component is ISyncModule -> {
                        channelFlow<SyncResult> {
                            val context = SyncModuleContext(this, group, store)
                            withTimeout(component.timeout) {
                                component.internalFlow(context).collect()
                            }
                        }
                        .onCompletion { t ->
                            if (t == null) emit(SyncResult.Success(skipped = false))
                        }
                        .catch { t ->
                            emit(SyncResult.Error(t))
                            if (component.required) throw SyncModuleException(component, t)
                        }
                        .map { progress ->
                            ComponentResult(component, progress, componentWeight)
                        }
                    }
                    component is ISyncGroup -> {
                        prepareGroupFlow(store, component, componentWeight)
                    }
                    else -> emptyFlow()
                }
            }
            .onCompletion { t ->
                if (t == null) emit(ComponentResult(group, SyncResult.Success(skipped = false), weight * group.weight))
            }
            .catch { t ->
                emit(ComponentResult(group, SyncResult.Error(t), weight * group.weight))
                if (group.required) throw SyncGroupException(group, t)
            }
    }
}