package com.arsylk.androidex.lib.domain.coroutines

import com.arsylk.androidex.lib.domain.holding.BagOfHolding
import com.arsylk.androidex.lib.domain.holding.setTagIfAbsentCompute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

const val BAG_SCOPE_KEY = "com.arsylk.androidex.lib.domain.coroutines.CoroutineScope"


val BagOfHolding.scope: CoroutineScope
    get() = setTagIfAbsentCompute<CoroutineScope>(BAG_SCOPE_KEY) {
        CloseableCoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    }


class CloseableCoroutineScope(context: CoroutineContext) : Closeable, CoroutineScope {
    override val coroutineContext: CoroutineContext = context

    override fun close() {
        coroutineContext.cancel()
    }
}
