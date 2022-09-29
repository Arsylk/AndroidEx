package com.arsylk.androidex.lib.domain.sync.module

import com.arsylk.androidex.lib.domain.sync.FlowModifier
import com.arsylk.androidex.lib.domain.sync.component.DSL
import com.arsylk.androidex.lib.domain.sync.component.ISyncComponentContext
import com.arsylk.androidex.lib.domain.sync.component.ISyncModule
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart


data class SyncModule(
    override val tag: String,
    override val required: Boolean,
    override val weight: Float,
    override val order: Int,
    override val condition: suspend ISyncComponentContext.() -> Boolean,
    override val timeout: Long,
    override val modifiers: List<FlowModifier>,
    val action: suspend SyncModuleContext.() -> Unit,
) : ISyncModule() {

    override fun flow(context: SyncModuleContext) =
        kotlinx.coroutines.flow.flow { emit(action.invoke(context)) }

    @DSL
    class Builder internal constructor() : SyncModuleBuilder() {
        private var action: suspend SyncModuleContext.() -> Unit = {}

        fun condition(block: suspend ISyncComponentContext.() -> Boolean) = apply { condition = block }

        fun action(block: suspend SyncModuleContext.() -> Unit) = apply { action = block }

        override fun build(): SyncModule {
            return SyncModule(tag, required, weight, order, condition, timeout, modifiers, action)
        }
    }
}


abstract class SyncModuleBuilder {
    var tag: String = "untagged"
    var required: Boolean = true
    var weight: Float = 1.0f
    var order: Int = 1
    var timeout: Long = Long.MAX_VALUE
    protected var condition: suspend ISyncComponentContext.() -> Boolean = { true }
    protected var modifiers = mutableListOf<FlowModifier>()

    private fun modify(block: FlowModifier) = apply { modifiers += block }

    fun onStart(block: suspend () -> Unit) = modify { onStart { block.invoke() } }

    fun onCompletion(block: suspend (t: Throwable?) -> Unit) = modify { onCompletion { block.invoke(it) } }

    abstract fun build(): ISyncModule
}
