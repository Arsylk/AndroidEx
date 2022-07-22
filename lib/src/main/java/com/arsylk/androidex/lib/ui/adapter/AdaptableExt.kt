package com.arsylk.androidex.lib.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding



@JvmName("inlineAdapterOfBinding")
inline fun <reified Item: Any, reified Binding: ViewBinding> inlineAdapterOf(
    noinline inflate: (layoutInflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean) -> Binding,
    noinline action: AdaptableBindingScope<Item, Binding>.() -> Unit,
) = AdaptableRecyclerAdapter<Item>().apply {
    adapt<Item, Binding> {
        inflate(inflate)
        bind(action)
    }
}

@JvmName("inlineAdapterOfView")
inline fun <reified Item: Any, reified ViewType: View> inlineAdapterOf(
    noinline inflate: (layoutInflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean) -> ViewType,
    noinline action: AdaptableViewScope<Item, ViewType>.() -> Unit,
) = AdaptableRecyclerAdapter<Item>().apply {
    adapt<Item, ViewType> {
        inflate(inflate)
        bind(action)
    }
}

@JvmName("inlineAdapterOfLayout")
inline fun <reified Item: Any> inlineAdapterOf(
    @LayoutRes layoutRes: Int,
    noinline action: AdaptableViewScope<Item, View>.() -> Unit,
) = AdaptableRecyclerAdapter<Item>().apply {
    adapt<Item>(layoutRes) {
        bind(action)
    }
}