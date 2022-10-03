package com.arsylk.androidex.lib.domain.sync.component

sealed interface SyncComponent {
    /** Unique in [RootSyncGroup] */
    val id: Int
    val tag: String
    val required: Boolean
    val weight: Float
    val order: Int
    val condition: suspend ISyncComponentContext.() -> Boolean get () = { true }
}


@DslMarker
annotation class DSL