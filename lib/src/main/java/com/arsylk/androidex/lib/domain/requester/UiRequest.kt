package com.arsylk.androidex.lib.domain.requester

interface UiRequest<Value> {
    val request: Request<Value>
    fun complete(value: Value) = request.complete(value)
}