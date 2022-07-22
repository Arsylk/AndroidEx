package com.arsylk.androidex.lib.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

sealed class AdaptableHolder(view: View): RecyclerView.ViewHolder(view) {
    open fun onViewAttachedToWindow() {}
    open fun onViewDetachedFromWindow() {}
    open fun onViewRecycled() {}
}
open class AdaptableViewHolder<ViewType: View>(val view: ViewType): AdaptableHolder(view) {}
open class AdaptableBindingHolder<Binding : ViewBinding>(val binding: Binding): AdaptableHolder(binding.root)

abstract class AdaptableCustomHolder<Item: Any>(view: View) : AdaptableHolder(view) {
    abstract fun bind(item: Item, position: Int, payloads: List<Any>?)
}