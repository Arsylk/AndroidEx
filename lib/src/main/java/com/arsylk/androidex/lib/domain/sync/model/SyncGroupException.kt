package com.arsylk.androidex.lib.domain.sync.model

import com.arsylk.androidex.lib.domain.sync.component.ISyncGroup

class SyncGroupException(
    val group: ISyncGroup,
    cause: Throwable,
) : Throwable(cause = cause)