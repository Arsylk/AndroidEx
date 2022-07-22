package com.arsylk.androidex.lib.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding


interface Adaptable<Item : Any> {
    val predicate: (Item) -> Boolean
    fun prepareViewHolder(viewGroup: ViewGroup): AdaptableHolder
    fun bindViewHolder(holder: AdaptableHolder, position: Int, item: Item, payloads: List<Any>?)
}

@Suppress("UNCHECKED_CAST")
class AdaptableView<Item : Any, Sub : Item, ViewType : View>(
    override val predicate: (Item) -> Boolean,
    val inflate: (layoutInflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean) -> ViewType,
    val onViewAttachedToWindow: AdaptableViewHolder<ViewType>.() -> Unit,
    val onViewDetachedFromWindow: AdaptableViewHolder<ViewType>.() -> Unit,
    val onViewRecycled: AdaptableViewHolder<ViewType>.() -> Unit,
    val bind: AdaptableViewScope<Sub, ViewType>.() -> Unit,
) : Adaptable<Item> {

    override fun prepareViewHolder(viewGroup: ViewGroup): AdaptableViewHolder<ViewType> {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflate.invoke(inflater, viewGroup, false)
        return object : AdaptableViewHolder<ViewType>(view) {
            override fun onViewAttachedToWindow() {
                onViewAttachedToWindow(this)
            }

            override fun onViewDetachedFromWindow() {
                onViewDetachedFromWindow(this)
            }

            override fun onViewRecycled() {
                onViewRecycled(this)
            }
        }
    }

    override fun bindViewHolder(holder: AdaptableHolder, position: Int, item: Item, payloads: List<Any>?) {
        val castedHolder = (holder as AdaptableViewHolder<ViewType>)
        val scope = AdaptableViewScope<Sub, ViewType>(
            holder = castedHolder,
            view = castedHolder.view,
            item = (item as Sub),
            position = position,
            payloads = payloads,
        )
        bind.invoke(scope)
    }

    @AdaptableDsl
    class Builder<Item : Any, Sub : Item, ViewType : View> {
        private lateinit var predicate: (Item) -> Boolean
        private lateinit var inflate: (layoutInflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean) -> ViewType
        private var onAttached: AdaptableViewHolder<ViewType>.() -> Unit = {}
        private var onDetached: AdaptableViewHolder<ViewType>.() -> Unit = {}
        private var onRecycled: AdaptableViewHolder<ViewType>.() -> Unit = {}
        private var bind: AdaptableViewScope<Sub, ViewType>.() -> Unit = {}

        fun predicate(block: (Item) -> Boolean) {
            this.predicate = block
        }

        fun inflate(block: (layoutInflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean) -> ViewType) {
            this.inflate = block
        }

        fun onAttached(block: AdaptableViewHolder<ViewType>.() -> Unit) {
            this.onAttached = block
        }

        fun onDetached(block: AdaptableViewHolder<ViewType>.() -> Unit) {
            this.onDetached = block
        }

        fun onRecycled(block: AdaptableViewHolder<ViewType>.() -> Unit) {
            this.onRecycled = block
        }

        fun bind(block: AdaptableViewScope<Sub, ViewType>.() -> Unit) {
            this.bind = block
        }


        fun build() = AdaptableView<Item, Sub, ViewType>(
            predicate = predicate,
            inflate = inflate,
            onViewAttachedToWindow = onAttached,
            onViewDetachedFromWindow = onDetached,
            onViewRecycled = onRecycled,
            bind = bind,
        )
    }
}

@Suppress("UNCHECKED_CAST")
class AdaptableBinding<Item : Any, Sub : Item, Binding : ViewBinding>(
    override val predicate: (Item) -> Boolean,
    val inflate: (layoutInflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean) -> Binding,
    val onViewAttachedToWindow: AdaptableBindingHolder<Binding>.() -> Unit = {},
    val onViewDetachedFromWindow: AdaptableBindingHolder<Binding>.() -> Unit = {},
    val onViewRecycled: AdaptableBindingHolder<Binding>.() -> Unit = {},
    val action: AdaptableBindingScope<Sub, Binding>.() -> Unit,
) : Adaptable<Item> {

    override fun prepareViewHolder(viewGroup: ViewGroup): AdaptableBindingHolder<Binding> {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = inflate.invoke(inflater, viewGroup, false)
        return object : AdaptableBindingHolder<Binding>(binding) {
            override fun onViewAttachedToWindow() {
                onViewAttachedToWindow(this)
            }

            override fun onViewDetachedFromWindow() {
                onViewDetachedFromWindow(this)
            }

            override fun onViewRecycled() {
                onViewRecycled(this)
            }
        }
    }

    override fun bindViewHolder(holder: AdaptableHolder, position: Int, item: Item, payloads: List<Any>?) {
        val castedHolder = (holder as AdaptableBindingHolder<Binding>)
        val scope = AdaptableBindingScope<Sub, Binding>(
            holder = castedHolder,
            binding = castedHolder.binding,
            item = (item as Sub),
            position = position,
            payloads = payloads,
        )
        action.invoke(scope)
    }

    @AdaptableDsl
    class Builder<Item : Any, Sub : Item, Binding : ViewBinding> {
        private lateinit var predicate: (Item) -> Boolean
        private lateinit var inflate: (layoutInflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean) -> Binding
        private var onAttached: AdaptableBindingHolder<Binding>.() -> Unit = {}
        private var onDetached: AdaptableBindingHolder<Binding>.() -> Unit = {}
        private var onRecycled: AdaptableBindingHolder<Binding>.() -> Unit = {}
        private var action: AdaptableBindingScope<Sub, Binding>.() -> Unit = {}

        fun predicate(block: (Item) -> Boolean) {
            this.predicate = block
        }

        fun inflate(block: (layoutInflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean) -> Binding) {
            this.inflate = block
        }

        fun onAttached(block: AdaptableBindingHolder<Binding>.() -> Unit) {
            this.onAttached = block
        }

        fun onDetached(block: AdaptableBindingHolder<Binding>.() -> Unit) {
            this.onDetached = block
        }

        fun onRecycled(block: AdaptableBindingHolder<Binding>.() -> Unit) {
            this.onRecycled = block
        }

        fun bind(block: AdaptableBindingScope<Sub, Binding>.() -> Unit) {
            this.action = block
        }


        fun build() = AdaptableBinding<Item, Sub, Binding>(
            predicate = predicate,
            inflate = inflate,
            onViewAttachedToWindow = onAttached,
            onViewDetachedFromWindow = onDetached,
            onViewRecycled = onRecycled,
            action = action,
        )
    }
}

@Suppress("UNCHECKED_CAST")
class AdaptableCustom<Item : Any, Sub : Item>(
    override val predicate: (Item) -> Boolean,
    val prepareHolder: (inflater: LayoutInflater, viewGroup: ViewGroup) -> AdaptableCustomHolder<Sub>,
) : Adaptable<Item> {

    override fun prepareViewHolder(viewGroup: ViewGroup): AdaptableCustomHolder<Sub> {
        val inflater = LayoutInflater.from(viewGroup.context)
        return prepareHolder(inflater, viewGroup)
    }

    override fun bindViewHolder(holder: AdaptableHolder, position: Int, item: Item, payloads: List<Any>?) {
        val castedHolder = (holder as AdaptableCustomHolder<Sub>)
        castedHolder.bind(item as Sub, position, payloads)
    }

    @AdaptableDsl
    class Builder<Item : Any, Sub : Item> {
        private lateinit var predicate: (Item) -> Boolean
        private lateinit var prepareHolder: (inflater: LayoutInflater, viewGroup: ViewGroup) -> AdaptableCustomHolder<Sub>

        fun predicate(block: (Item) -> Boolean) {
            this.predicate = block
        }

        fun prepareHolder(block: (inflater: LayoutInflater, viewGroup: ViewGroup) -> AdaptableCustomHolder<Sub>) {
            this.prepareHolder = block
        }


        fun build() = AdaptableCustom<Item, Sub>(
            predicate = predicate,
            prepareHolder = prepareHolder,
        )
    }
}

