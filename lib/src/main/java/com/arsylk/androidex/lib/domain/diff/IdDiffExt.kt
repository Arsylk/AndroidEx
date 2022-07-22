package com.arsylk.androidex.lib.domain.diff

import com.arsylk.androidex.lib.model.diff.Id


inline fun <T : Id> idDiffItemCallback(
    crossinline comparator: (T, T) -> Boolean = { t1, t2 -> t1 == t2 },
) = object : IdDiff.ItemCallback<T>() {
    override fun areContentsTheSame(oldItem: T, newItem: T) = comparator(oldItem, newItem)
}

inline fun <T : Id> idDiffCallback(
    oldList: List<T>,
    newList: List<T>,
    crossinline comparator: (T, T) -> Boolean = { t1, t2 -> t1 == t2 },
) = object : IdDiff.Callback<T>(oldList, newList) {
    override fun areContentsTheSame(oldItem: T, newItem: T) = comparator(oldItem, newItem)
}