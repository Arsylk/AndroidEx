package com.arsylk.androidex.lib.domain.lifecycle

import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

interface LifecycleEx : LifecycleOwner {
    val lifecycleOwnerEx: LifecycleOwner
        get() = (this as? Fragment)?.viewLifecycleOwner ?: this

    /**
     * Given [block] will run when lifecycle enters at least [state],
     * suspend when state falls below it,
     * and cancel when lifecycle is destroyed
     */
    fun launchWhen(
        context: CoroutineContext = EmptyCoroutineContext,
        state: Lifecycle.State,
        block: suspend CoroutineScope.() -> Unit,
    ): Job = lifecycleOwnerEx.lifecycleScope.launch(context) {
        lifecycle.whenStateAtLeast(state, block)
    }

    /**
     * Collecting of [this] will begin when lifecycle enters at least [state],
     * collector will suspend when state falls below it,
     * and [this] will cancel when lifecycle is destroyed
     */
    fun <T> Flow<T>.collectWhen(
        context: CoroutineContext = EmptyCoroutineContext,
        state: Lifecycle.State,
        block: suspend (T) -> Unit,
    ): Job = launchWhen(context, state) {
        collect { block.invoke(it) }
    }

    fun <T> Flow<T>.collectLatestWhen(
        context: CoroutineContext = EmptyCoroutineContext,
        state: Lifecycle.State,
        block: suspend (T) -> Unit,
    ): Job = launchWhen(context, state) {
        collectLatest(block)
    }


    fun launchWhenResumed(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend CoroutineScope.() -> Unit,
    ): Job = launchWhen(context, Lifecycle.State.RESUMED, block)

    fun <T> Flow<T>.collectWhenResumed(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend (T) -> Unit,
    ): Job = collectWhen(context, Lifecycle.State.RESUMED, block)

    fun <T> Flow<T>.collectLatestWhenResumed(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend (T) -> Unit,
    ): Job = collectLatestWhen(context, Lifecycle.State.RESUMED, block)

    /**
     * Given [block] will run every time when lifecycle enters at least [state],
     * and canel when state falls below it or lifecycle is destroyed
     */
    fun repeatOn(
        context: CoroutineContext = EmptyCoroutineContext,
        state: Lifecycle.State,
        block: suspend CoroutineScope.() -> Unit,
    ): Job = lifecycleOwnerEx.lifecycleScope.launch(context) {
        lifecycleOwnerEx.lifecycle.repeatOnLifecycle(state, block)
    }

    /**
     * Collecting of [this] will start every time lifecycle enters at least [state],
     * cancel when state falls below it or lifecycle is destroyed
     * lookup [flowWithLifecycle] for more information
     */
    fun <T> Flow<T>.collectOnLifecycle(
        context: CoroutineContext = EmptyCoroutineContext,
        state: Lifecycle.State,
        block: suspend (T) -> Unit,
    ): Job = lifecycleOwnerEx.lifecycleScope.launch(context) {
        flowWithLifecycle(lifecycleOwnerEx.lifecycle, state)
            .collect { block.invoke(it) }
    }

    fun <T> Flow<T>.collectLatestOnLifecycle(
        context: CoroutineContext = EmptyCoroutineContext,
        state: Lifecycle.State,
        block: suspend (T) -> Unit,
    ): Job = lifecycleOwnerEx.lifecycleScope.launch(context) {
        flowWithLifecycle(lifecycleOwnerEx.lifecycle, state)
            .collectLatest(block)
    }


    fun repeatOnResumed(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend CoroutineScope.() -> Unit,
    ): Job = repeatOn(context, Lifecycle.State.RESUMED, block)

    fun <T> Flow<T>.collectOnResumed(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend (T) -> Unit,
    ): Job = collectOnLifecycle(context, Lifecycle.State.RESUMED, block)

    fun <T> Flow<T>.collectLatestOnResumed(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend (T) -> Unit,
    ): Job = collectLatestOnLifecycle(context, Lifecycle.State.RESUMED, block)
}