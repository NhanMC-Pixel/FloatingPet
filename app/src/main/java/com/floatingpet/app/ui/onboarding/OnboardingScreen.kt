package com.floatingpet.app.ui.onboarding

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floatingpet.core.util.PermissionHelper

@Composable
fun OnboardingScreen(onOnboardingComplete: () -> Unit) {
    val context = LocalContext.current
    var step by remember { mutableIntStateOf(1) } // 1 = overlay, 2 = notification listener

    // Overlay permission launcher (only used on < M)
    val overlayPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { /* not used, SYSTEM_ALERT_WINDOW is special */ }

    // Check current state
    val overlayGranted = PermissionHelper.canDrawOverlays(context)
    val notificationGranted = PermissionHelper.isNotificationListenerEnabled(context)

    // Automatically move to next step if permission already granted
    LaunchedEffect(step, overlayGranted, notificationGranted) {
        if (step == 1 && overlayGranted) step = 2
        if (step == 2 && notificationGranted) {
            onOnboardingComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to Floating Pet!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        when (step) {
            1 -> {
                Text("1. Grant overlay permission", fontSize = 20.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "This allows the pet to float on top of other apps.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = {
                    if (!overlayGranted) {
                        PermissionHelper.requestOverlayPermission(context)
                    } else {
                        step = 2
                    }
                }) {
                    Text(if (overlayGranted) "Already granted" else "Grant Overlay Permission")
                }
            }
            2 -> {
                Text("2. Grant notification access", fontSize = 20.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "So the pet can react to your notifications.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = {
                    if (!notificationGranted) {
                        PermissionHelper.openNotificationListenerSettings(context)
                    } else {
                        onOnboardingComplete()
                    }
                }) {
                    Text(if (notificationGranted) "Already granted" else "Grant Notification Access")
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Skip / continue button (if user wants to skip, just complete)
        TextButton(onClick = { onOnboardingComplete() }) {
            Text("Skip for now")
        }
    }
}