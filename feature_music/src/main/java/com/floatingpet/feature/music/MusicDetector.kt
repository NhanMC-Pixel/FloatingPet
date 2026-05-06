package com.floatingpet.feature.music

import android.content.ComponentName
import android.content.Context
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Handler
import android.os.Looper
import com.floatingpet.core.util.PreferencesHelper
import com.floatingpet.domain.PetEvent
import com.floatingpet.domain.PetEventBus
import kotlinx.coroutines.*

class MusicDetector(private val context: Context) {

    private val mediaSessionManager: MediaSessionManager =
        context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager

    private val handler = Handler(Looper.getMainLooper())
    private var isListening = false
    private var currentDance = false
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val sessionsListener = MediaSessionManager.OnActiveSessionsChangedListener { controllers ->
        val targetPackage = PreferencesHelper.getMusicAppPackage(context)
        val playing = controllers?.any { controller ->
            controller.packageName == targetPackage &&
                    controller.playbackState?.state == PlaybackState.STATE_PLAYING
        } ?: false
        if (playing && !currentDance) {
            currentDance = true
            scope.launch { PetEventBus.send(PetEvent.StartDance) }
        } else if (!playing && currentDance) {
            currentDance = false
            scope.launch { PetEventBus.send(PetEvent.StopDance) }
        }
    }

    fun start() {
        if (isListening) return
        val componentName = ComponentName(context, "com.floatingpet.feature.notifications.PetNotificationListenerService")
        @Suppress("DEPRECATION")
        mediaSessionManager.addOnActiveSessionsChangedListener(
            sessionsListener,
            componentName,
            handler
        )
        isListening = true
    }

    fun stop() {
        if (!isListening) return
        mediaSessionManager.removeOnActiveSessionsChangedListener(sessionsListener)
        isListening = false
    }
}
