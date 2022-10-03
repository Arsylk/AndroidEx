package com.arsylk.androidex.app

import com.arsylk.androidex.app.databinding.ItemSyncBinding
import com.arsylk.androidex.lib.domain.sync.SimpleProgress
import com.arsylk.androidex.lib.domain.sync.SyncStateStore
import com.arsylk.androidex.lib.domain.sync.component.SyncComponent
import com.arsylk.androidex.lib.domain.sync.group.RootSyncGroup
import com.arsylk.androidex.lib.domain.sync.iterate
import com.arsylk.androidex.lib.domain.sync.model.SyncResult
import com.arsylk.androidex.lib.model.diff.IntId
import com.arsylk.androidex.lib.ui.adapter.AdaptableRecyclerAdapter
import com.arsylk.androidex.lib.ui.diff.idDiffUtilList

class Adapter : AdaptableRecyclerAdapter<Item>() {
    override var items: List<Item> by idDiffUtilList()
    private var store: SyncStateStore? = null

    fun submitData(root: RootSyncGroup, store: SyncStateStore) {
        this.store = store
        items = root.iterate(sort = true).map { Item(it) }
    }

    fun updateProgress(component: SyncComponent) {
        items.forEachIndexed { i, item ->
            if (item.id == component.id) {
                notifyItemChanged(i, Any())
            }
        }
    }

    init {
        val adapter = this
        adapt<Item, ItemSyncBinding> {
            inflate(ItemSyncBinding::inflate)
            bind {
                binding.title.text = item.component.tag
                binding.message.text = when (val r = adapter.store?.get(item.component)?.result) {
                    is SyncResult.Success, is SyncResult.Error -> r.toString()
                    is SyncResult.Progress -> "Progress(${r.percentage})"
                    null -> null
                }
            }
        }
    }
}

data class Item(
    val component: SyncComponent,
) : IntId {
    override val id: Int get() = component.id
}