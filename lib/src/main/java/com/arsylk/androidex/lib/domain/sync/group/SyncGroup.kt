package com.arsylk.androidex.lib.domain.sync.group

import com.arsylk.androidex.lib.domain.sync.component.ISyncComponentContext
import com.arsylk.androidex.lib.domain.sync.component.ISyncGroup
import com.arsylk.androidex.lib.domain.sync.component.DSL
import com.arsylk.androidex.lib.domain.sync.component.SyncComponent
import com.arsylk.androidex.lib.domain.sync.module.SyncModule




class SyncGroup(
    override val tag: String,
    override val concurrency: Int,
    override val required: Boolean,
    override val weight: Float,
    override val order: Int,
    override val condition: suspend ISyncComponentContext.() -> Boolean,
    override val components: List<SyncComponent>,
) : ISyncGroup() {

    @DSL
    class Builder internal constructor() : SyncGroupBuilder() {
        var tag: String = "untagged"
        var concurrency: Int = 1
        var required: Boolean = false
        var weight: Float = 1.0f
        var order: Int = 1
        private var condition: suspend ISyncComponentContext.() -> Boolean = { true }

        fun condition(block: suspend ISyncComponentContext.() -> Boolean) = apply { condition = block }

        override fun build() = SyncGroup(tag, concurrency, required, weight, order, condition, components)
    }
}


abstract class SyncGroupBuilder {
    protected val components = mutableListOf<SyncComponent>()

    fun group(block: SyncGroup.Builder.() -> Unit) {
        val group = SyncGroup.Builder().apply(block).build()
        addComponent(group)
    }

    fun module(block: SyncModule.Builder.() -> Unit) {
        val module = SyncModule.Builder().apply(block).build()
        addComponent(module)
    }

    fun addComponent(component: SyncComponent) { components += component }

    abstract fun build(): ISyncGroup
}