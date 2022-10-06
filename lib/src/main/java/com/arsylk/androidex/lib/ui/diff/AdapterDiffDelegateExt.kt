package com.arsylk.androidex.lib.ui.diff

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.arsylk.androidex.lib.domain.diff.IdDiff
import com.arsylk.androidex.lib.domain.diff.idDiffCallback
import com.arsylk.androidex.lib.model.diff.Id


fun <T: Any> diffUtilList(
    callback: (oldList: List<T>, newList: List<T>) -> DiffUtil.Callback
) = AdapterDiffDelegate(callback)

inline fun <T: Any> simpleDiffUtilList(
    crossinline areItemsTheSame: (oldItem: T, newItem: T) -> Boolean = { _, _ -> true },
    crossinline areContentsTheSame: (oldItem: T, newItem: T) -> Boolean,
) = diffUtilList(callback = { oldList: List<T>, newList: List<T> ->
    object : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return areItemsTheSame(oldList[oldItemPosition], newList[newItemPosition])
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return areContentsTheSame(oldList[oldItemPosition], newList[newItemPosition])
        }
    }
})


inline fun <T : Id> idDiffUtilList(
    crossinline comparator: (T, T) -> Boolean = { t1, t2 -> t1 == t2 },
) = diffUtilList<T>(
    callback = { oldList, newList ->
        idDiffCallback(oldList, newList, comparator)
    }
)