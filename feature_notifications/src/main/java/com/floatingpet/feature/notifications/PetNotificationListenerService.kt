package com.floatingpet.feature.notifications

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.floatingpet.domain.PetEvent
import com.floatingpet.domain.PetEventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PetNotificationListenerService : NotificationListenerService() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        val notification = sbn.notification
        val extras = notification.extras
        val title = extras.getString("android.title") ?: ""
        val text = extras.getString("android.text") ?: ""
        val appName = sbn.packageName // a bit raw, but works

        if (title.isNotEmpty() || text.isNotEmpty()) {
            scope.launch {
                PetEventBus.send(
                    PetEvent.ShowNotificationBubble(
                        appName = appName,
                        content = if (text.isNotEmpty()) text else title
                    )
                )
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) { /* ignore */ }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}