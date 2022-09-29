package com.arsylk.androidex.lib.domain.sync.group

import com.arsylk.androidex.lib.domain.sync.component.ISyncComponentContext
import com.arsylk.androidex.lib.domain.sync.SyncGroupStore

class SyncComponentContext(
    override val store: SyncGroupStore
) : ISyncComponentContext