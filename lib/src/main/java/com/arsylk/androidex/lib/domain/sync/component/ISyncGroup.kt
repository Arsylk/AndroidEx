package com.arsylk.androidex.lib.domain.sync.component


abstract class ISyncGroup : SyncComponent {
    final override var id: Int = 0
        internal set
    open val concurrency: Int = 1
    open val components: List<SyncComponent> = emptyList()

    override fun toString() = "ISyncGroup($tag)"
}