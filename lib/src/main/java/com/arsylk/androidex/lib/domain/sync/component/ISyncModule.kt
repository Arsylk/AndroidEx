package com.arsylk.androidex.lib.domain.sync.component

import com.arsylk.androidex.lib.domain.sync.FlowModifier
import com.arsylk.androidex.lib.domain.sync.module.SyncModuleContext
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

abstract class ISyncModule : SyncComponent {
    final override var id: Int = 0
        internal set
    open val modifiers: List<FlowModifier> = emptyList()
    open val timeout: Long = Long.MAX_VALUE


    open fun flow(context: SyncModuleContext): Flow<Any> = flow {}

    internal fun internalFlow(context: SyncModuleContext): Flow<Any> {
        var builtFlow = flow(context)
        modifiers.forEach {
            builtFlow = builtFlow.let(it)
        }
        return builtFlow
    }

    override fun toString() = "ISyncModule($tag)"
}