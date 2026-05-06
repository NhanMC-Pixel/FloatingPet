package com.floatingpet.domain

sealed class PetEvent {
    data class ShowNotificationBubble(
        val appName: String,
        val content: String
    ) : PetEvent()

    object StartDance : PetEvent()
    object StopDance : PetEvent()
}