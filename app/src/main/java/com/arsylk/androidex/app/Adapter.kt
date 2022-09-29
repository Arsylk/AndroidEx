package com.arsylk.androidex.app

import com.arsylk.androidex.app.databinding.ItemSyncBinding
import com.arsylk.androidex.lib.domain.sync.SimpleProgress
import com.arsylk.androidex.lib.domain.sync.SyncStateStore
import com.arsylk.androidex.lib.domain.sync.component.ISyncGroup
import com.arsylk.androidex.lib.domain.sync.component.SyncComponent
import com.arsylk.androidex.lib.domain.sync.group.RootSyncGroup
import com.arsylk.androidex.lib.domain.sync.iterate
import com.arsylk.androidex.lib.domain.sync.model.SyncResult
import com.arsylk.androidex.lib.model.diff.IntId
import com.arsylk.androidex.lib.ui.adapter.AdaptableRecyclerAdapter
import com.arsylk.androidex.lib.ui.diff.idDiffUtilList

class Adapter : AdaptableRecyclerAdapter<Item>() {
    override var items: List<Item> by idDiffUtilList()

    fun submitData(root: RootSyncGroup, store: SyncStateStore) {
        items = root.iterate(sort = true).map { Item(it, store[it]) }
    }

    fun setProgress(pair: Pair<SyncComponent, SimpleProgress>) {
        items.forEachIndexed { i, item ->
            if (item.id == pair.first.id) {
                item.p = pair.second
                notifyItemChanged(i, Any())
            }
        }
    }

    init {
        adapt<Item, ItemSyncBinding> {
            inflate(ItemSyncBinding::inflate)
            bind {
                binding.title.text = item.component.tag
                binding.message.text = when (val r = item.p?.result) {
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
    var p: SimpleProgress?,
) : IntId {
    override val id: Int get() = component.id
}