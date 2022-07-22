package com.arsylk.androidex.lib.ui.adapter

import android.view.View
import androidx.viewbinding.ViewBinding

@AdaptableDsl
sealed interface AdaptableScope<Item : Any> {
    val holder: AdaptableHolder
    val item: Item
    val position: Int
    val payloads: List<Any>?
}

class AdaptableViewScope<Item: Any, ViewType: View>(
    override val holder: AdaptableViewHolder<ViewType>,
    val view: ViewType,
    override val position: Int,
    override val item: Item,
    override val payloads: List<Any>?
): AdaptableScope<Item>

class AdaptableBindingScope<Item : Any, Binding : ViewBinding>(
    override val holder: AdaptableBindingHolder<Binding>,
    val binding: Binding,
    override val position: Int,
    override val item: Item,
    override val payloads: List<Any>?
): AdaptableScope<Item>