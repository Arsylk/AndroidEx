package com.arsylk.androidex.lib.domain.sync.model

sealed class SyncResult {
    abstract val percentage: Float

    data class Success(
        val skipped: Boolean = false,
    ) : SyncResult() {
        override val percentage = 100.0f
    }
    data class Progress(
        override val percentage: Float,
        val message: String? = null,
    ) : SyncResult()
    data class Error(
        val throwable: Throwable,
    ) : SyncResult() {
        override val percentage = 100.0f
    }
}