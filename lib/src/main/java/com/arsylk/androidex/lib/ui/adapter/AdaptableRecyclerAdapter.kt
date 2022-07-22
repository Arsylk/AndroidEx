package com.arsylk.androidex.lib.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import java.lang.ref.WeakReference


@AdaptableDsl
open class AdaptableRecyclerAdapter<Item : Any> : RecyclerView.Adapter<AdaptableHolder>() {
    // ViewType -> Adaptable
    private val adaptableMap = mutableMapOf<Int, Adaptable<Item>>()
    private var recyclerViewRef: WeakReference<RecyclerView> = WeakReference(null)
    val recyclerView: RecyclerView? get() = recyclerViewRef.get()
    open var items: List<Item> = emptyList()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerViewRef = WeakReference(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclerViewRef.clear()
    }


    fun clearAdaptableMap() {
        synchronized(this) {
            adaptableMap.clear()
        }
    }

    fun addAdaptable(adaptable: Adaptable<Item>) {
        synchronized(this) {
            val viewType = adaptableMap.keys.maxOrNull()?.plus(1) ?: 0
            adaptableMap[viewType] = adaptable
        }
    }

    private fun getAdaptableByViewType(viewType: Int): Adaptable<Item> {
        return adaptableMap[viewType] ?: throw AdaptableNotFoundByViewType(viewType)
    }

    private fun getAdaptableByPosition(position: Int): Adaptable<Item> {
        return adaptableMap.values
            .firstOrNull { it.predicate(getItem(position)) }
            ?: throw AdaptableNotFoundByPosition(position)
    }

    override fun getItemViewType(position: Int): Int {
        return adaptableMap
            .firstNotNullOfOrNull { (k, v) -> k.takeIf { v.predicate(getItem(position)) } }
            ?: throw AdaptableViewTypeNotFound(position)
    }

    open fun getItem(position: Int): Item = items[position]

    override fun getItemCount(): Int = items.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdaptableHolder {
        val adaptable = getAdaptableByViewType(viewType)
        return adaptable.prepareViewHolder(parent)
    }

    override fun onBindViewHolder(holder: AdaptableHolder, position: Int) {
        onBindViewHolderInternal(holder, position, null)
    }

    override fun onBindViewHolder(
        holder: AdaptableHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        onBindViewHolderInternal(holder, position, payloads)
    }

    private fun onBindViewHolderInternal(
        holder: AdaptableHolder,
        position: Int,
        payloads: List<Any>?,
    ) {
        val adaptable = getAdaptableByPosition(position)
        adaptable.bindViewHolder(holder, position, getItem(position), payloads)
    }

    override fun onViewAttachedToWindow(holder: AdaptableHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onViewAttachedToWindow()
    }

    override fun onViewDetachedFromWindow(holder: AdaptableHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onViewDetachedFromWindow()
    }

    override fun onViewRecycled(holder: AdaptableHolder) {
        super.onViewRecycled(holder)
        holder.onViewRecycled()
    }

    @JvmName("adaptBinding")
    inline fun <reified Sub: Item, reified Binding : ViewBinding> adapt(
        builder: AdaptableBinding.Builder<Item, Sub, Binding>.() -> Unit
    ) {
        val adaptable = AdaptableBinding.Builder<Item, Sub, Binding>()
            .apply { predicate { it is Sub } }
            .apply(builder)
            .build()
        addAdaptable(adaptable)
    }

    @JvmName("adaptView")
    inline fun <reified Sub: Item, reified ViewType : View> adapt(
        builder: AdaptableView.Builder<Item, Sub, ViewType>.() -> Unit,
    ) {
        val adaptable = AdaptableView.Builder<Item, Sub, ViewType>()
            .apply { predicate { it is Sub } }
            .apply(builder)
            .build()
        addAdaptable(adaptable)
    }

    @JvmName("adaptLayout")
    inline fun <reified Sub: Item> adapt(
        @LayoutRes layoutRes: Int,
        builder: AdaptableView.Builder<Item, Sub, View>.() -> Unit,
    ) {

        val adaptable = AdaptableView.Builder<Item, Sub, View>()
            .apply {
                predicate { it is Sub }
                inflate { layoutInflater, parent, attachToParent ->
                    layoutInflater.inflate(layoutRes, parent, attachToParent)
                }
            }
            .apply(builder)
            .build()
        addAdaptable(adaptable)
    }

    @JvmName("adaptCustom")
    inline fun <reified Sub: Item> adaptCustom(
        builder: AdaptableCustom.Builder<Item, Sub>.() -> Unit
    ) {
        val adaptable = AdaptableCustom.Builder<Item, Sub>()
            .apply { predicate { it is Sub } }
            .apply(builder)
            .build()
        addAdaptable(adaptable)
    }
}