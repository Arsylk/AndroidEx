package com.arsylk.androidex.lib.domain.holding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arsylk.androidex.lib.domain.coroutines.BAG_SCOPE_KEY
import com.arsylk.androidex.lib.domain.coroutines.CloseableCoroutineScope
import kotlinx.coroutines.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


fun BagOfHolding(): BagOfHolding = BagOfHoldingImpl()

fun BagOfHolding(vararg items: Pair<String, Any>): BagOfHolding {
    val bag = BagOfHolding()
    items.forEach { (k, v) ->
        bag.setTagIfAbsent(k, v)
    }
    return bag
}

fun CoroutineScope.scopeBag(): BagOfHolding {
    val bag = BagOfHolding(BAG_SCOPE_KEY to this)
    launch { awaitCancellation() }.invokeOnCompletion { bag.close() }
    return bag
}

fun ViewModel.viewModelBag() = lazy { viewModelScope.scopeBag() }

@OptIn(ExperimentalContracts::class)
inline fun <T> BagOfHolding.setTagIfAbsentCompute(key: String, produceValue: () -> T): T {
    contract { callsInPlace(produceValue, kind = InvocationKind.AT_MOST_ONCE) }
    val current: T? = getTag(key)
    if (current != null) return current
    return setTagIfAbsent(key, produceValue())
}