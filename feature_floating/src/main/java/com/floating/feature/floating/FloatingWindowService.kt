package com.floatingpet.feature.floating

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.floatingpet.domain.PetEvent
import com.floatingpet.domain.PetEventBus
import com.floatingpet.domain.PetState
import com.floatingpet.feature.music.MusicDetector
import kotlinx.coroutines.*

class FloatingWindowService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var petView: PetView
    private lateinit var animationManager: AnimationManager
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var bubbleView: MangaBubbleView? = null
    private var bubbleRemovalJob: Job? = null
    private lateinit var musicDetector: MusicDetector
    private var lastNonForcedState: PetState = PetState.IDLE

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        petView = PetView(this)
        animationManager = AnimationManager { state -> petView.setState(state) }

        musicDetector = MusicDetector(this)

        // Collect events from event bus
        serviceScope.launch {
            PetEventBus.events.collect { event ->
                when (event) {
                    is PetEvent.ShowNotificationBubble -> showBubble(event.content)
                    PetEvent.StartDance -> forceDanceState(true)
                    PetEvent.StopDance -> forceDanceState(false)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = buildNotification()
        startForeground(NOTIFICATION_ID, notification)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 200
        }

        windowManager.addView(petView, params)

        petView.setDragListener { dx, dy ->
            params.x += dx
            params.y += dy
            windowManager.updateViewLayout(petView, params)
        }

        animationManager.start()
        musicDetector.start()

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        animationManager.stop()
        musicDetector.stop()
        serviceScope.cancel()
        removeBubble()
        if (::petView.isInitialized) windowManager.removeView(petView)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun showBubble(content: String) {
        removeBubble()
        val bubble = MangaBubbleView(this).apply { setText(content) }
        bubbleView = bubble

        val bubbleParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            // Place near pet (just to the right)
            x = 250
            y = 200
        }
        windowManager.addView(bubble, bubbleParams)

        // Auto‑remove after 5 seconds
        bubbleRemovalJob?.cancel()
        bubbleRemovalJob = serviceScope.launch {
            delay(5000)
            removeBubble()
        }
    }

    private fun removeBubble() {
        bubbleRemovalJob?.cancel()
        bubbleView?.let {
            windowManager.removeView(it)
        }
        bubbleView = null
    }

    private fun forceDanceState(dance: Boolean) {
        if (dance) {
            // Remember current state to restore later
            // We can't easily get current PetState from petView without exposing it,
            // so we'll track in AnimationManager forced state
            animationManager.setForcedState(PetState.DANCE)
        } else {
            animationManager.setForcedState(null) // resume random cycle
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Floating Pet",
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Keeps the pet running" }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Floating Pet")
            .setContentText("Your pet is floating")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "floating_pet_channel"
        private const val NOTIFICATION_ID = 101

        fun start(context: Context) {
            val intent = Intent(context, FloatingWindowService::class.java)
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, FloatingWindowService::class.java)
            context.stopService(intent)
        }
    }
}