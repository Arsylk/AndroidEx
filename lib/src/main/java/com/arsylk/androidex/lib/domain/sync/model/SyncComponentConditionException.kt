package com.arsylk.androidex.lib.domain.sync.model

import com.arsylk.androidex.lib.domain.sync.component.SyncComponent

class SyncComponentConditionException(
    val component: SyncComponent,
    cause: Throwable
) : Throwable(cause = cause)