package com.arsylk.androidex.lib.domain.sync.group

import com.arsylk.androidex.lib.domain.sync.component.ISyncGroup
import com.arsylk.androidex.lib.domain.sync.SyncGroupStore
import com.arsylk.androidex.lib.domain.sync.component.DSL
import com.arsylk.androidex.lib.domain.sync.component.SyncComponent


class RootSyncGroup(
    override val components: List<SyncComponent>,
) : ISyncGroup() {
    override val tag = "root"
    override val required = true
    override val weight = 1.0f
    override val order = 1
    override val concurrency = 1
    val store: SyncGroupStore = SyncGroupStore()

    @DSL
    class Builder internal constructor() : SyncGroupBuilder() {

        override fun build(): RootSyncGroup {
            val root = RootSyncGroup(components)

            var i = 0
            fun iterate(component: SyncComponent) {
                component.id = i++
                if (component is ISyncGroup) {
                    component.components.forEach(::iterate)
                }
            }
            iterate(root)

            return root
        }
    }
}