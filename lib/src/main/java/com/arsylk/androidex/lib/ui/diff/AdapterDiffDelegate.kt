package com.arsylk.androidex.lib.ui.diff

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KProperty

class AdapterDiffDelegate<T: Any>(
    private val getCallback: (oldList: List<T>, newList: List<T>) -> DiffUtil.Callback
) {
    private var field = emptyList<T>()

    operator fun setValue(thisRef: RecyclerView.Adapter<*>, kProperty: KProperty<*>, value: List<T>) {
        val callback = getCallback(field, value)
        field = value
        DiffUtil.calculateDiff(callback).dispatchUpdatesTo(thisRef)
    }

    operator fun getValue(thisRef: RecyclerView.Adapter<*>, kProperty: KProperty<*>) = field
}
