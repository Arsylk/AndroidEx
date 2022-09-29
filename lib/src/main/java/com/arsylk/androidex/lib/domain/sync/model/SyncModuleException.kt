package com.arsylk.androidex.lib.domain.sync.model

import com.arsylk.androidex.lib.domain.sync.component.ISyncModule

class SyncModuleException(
    val module: ISyncModule,
    cause: Throwable,
) : Throwable(cause = cause)