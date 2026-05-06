package com.floatingpet.feature.floating

import com.floatingpet.domain.PetState
import kotlinx.coroutines.*

class AnimationManager(
    private val onStateChanged: (PetState) -> Unit
) {
    private var job: Job? = null
    private val states = PetState.values().filter { it != PetState.DRAG }
    private var currentIndex = 0
    private var forcedState: PetState? = null

    fun setForcedState(state: PetState?) {
        forcedState = state
        // Immediately apply if not null
        forcedState?.let { onStateChanged(it) }
    }

    fun start() {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                if (forcedState != null) {
                    delay(500) // check again soon
                } else {
                    val nextState = states[currentIndex % states.size]
                    onStateChanged(nextState)
                    currentIndex++
                    delay((3000L..5000L).random())
                }
            }
        }
    }

    fun stop() {
        job?.cancel()
    }
}