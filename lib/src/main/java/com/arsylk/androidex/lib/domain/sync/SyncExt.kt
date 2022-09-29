package com.arsylk.androidex.lib.domain.sync

import com.arsylk.androidex.lib.domain.sync.component.ISyncGroup
import com.arsylk.androidex.lib.domain.sync.component.SyncComponent
import com.arsylk.androidex.lib.domain.sync.group.RootSyncGroup
import kotlinx.coroutines.flow.Flow


typealias FlowModifier = Flow<Any>.() -> Flow<Any>

inline fun <T> Iterable<T>.sumOf(selector: (T) -> Float): Float {
    var sum = 0f
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

fun sync(block: RootSyncGroup.Builder.() -> Unit): RootSyncGroup {
    return RootSyncGroup.Builder().apply(block).build()
}

fun RootSyncGroup.iterate(sort: Boolean): List<SyncComponent> {
    val comparator = compareBy<SyncComponent> { it.order }.thenBy { it.id }
    return buildList {
        fun inner(component: SyncComponent) {
            add(component)
            if (component is ISyncGroup) {
                val list = if (sort) component.components.sortedWith(comparator)
                else component.components
                list.forEach(::inner)
            }
        }
        inner(this@iterate)
    }
}