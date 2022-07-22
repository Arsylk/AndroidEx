package com.arsylk.androidex.lib.domain.diff

import androidx.recyclerview.widget.DiffUtil
import com.arsylk.androidex.lib.model.diff.Id

object IdDiff {
    abstract class Callback<in T : Id>(
        private val oldList: List<T>,
        private val newList: List<T>,
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            areContentsTheSame(oldList[oldItemPosition], newList[newItemPosition])

        abstract fun areContentsTheSame(oldItem: T, newItem: T): Boolean
    }

    abstract class ItemCallback<in T : Id> : DiffUtil.ItemCallback<@UnsafeVariance T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem.id == newItem.id
    }
}