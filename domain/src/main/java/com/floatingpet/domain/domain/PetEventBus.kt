package com.floatingpet.domain

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object PetEventBus {
    private val _events = MutableSharedFlow<PetEvent>(extraBufferCapacity = 64)
    val events = _events.asSharedFlow()

    suspend fun send(event: PetEvent) {
        _events.emit(event)
    }
}