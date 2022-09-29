package com.arsylk.androidex.lib.domain.sync.module

import androidx.annotation.IntRange
import com.arsylk.androidex.lib.domain.sync.component.ISyncComponentContext
import com.arsylk.androidex.lib.domain.sync.component.ISyncGroup
import com.arsylk.androidex.lib.domain.sync.model.SyncResult
import com.arsylk.androidex.lib.domain.sync.SyncGroupStore
import kotlinx.coroutines.channels.SendChannel

class SyncModuleContext(
    private val progressChannel: SendChannel<SyncResult.Progress>,
    val group: ISyncGroup,
    override val store: SyncGroupStore,
) : ISyncComponentContext {

    suspend fun postProgress(@IntRange(from = 0, to = 100) progress: Int, message: String? = null) {
        progressChannel.send(SyncResult.Progress(progress.toFloat(), message))
    }
}