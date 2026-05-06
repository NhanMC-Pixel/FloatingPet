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
import java.util.concurrent.Executors

class MusicDetector(private val context: Context) {

    private val mediaSessionManager: MediaSessionManager =
        context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager

    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())
    private var isListening = false
    private var currentDance = false
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val sessionsListener = MediaSessionManager.OnActiveSessionsChangedListener { controllers ->
        val targetPackage = PreferencesHelper.getMusicAppPackage(context)
        val playing = controllers.any { controller ->
            controller.packageName == targetPackage &&
                    controller.playbackState?.state == PlaybackState.STATE_PLAYING
        }
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
        // requires API 31+, for older use deprecated method – but we target API 34 and min 24, we can add check
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            mediaSessionManager.addOnActiveSessionsChangedListener(
                executor,
                sessionsListener,
                componentName
            )
        } else {
            @Suppress("DEPRECATION")
            mediaSessionManager.addOnActiveSessionsChangedListener(
                sessionsListener,
                componentName,
                handler
            )
        }
        isListening = true
    }

    fun stop() {
        if (!isListening) return
        mediaSessionManager.removeOnActiveSessionsChangedListener(sessionsListener)
        isListening = false
    }
}