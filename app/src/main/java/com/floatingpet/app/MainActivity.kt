package com.floatingpet.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.floatingpet.core.util.PermissionHelper
import com.floatingpet.feature.floating.FloatingWindowService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetControlScreen()
        }
    }
}

@Composable
fun PetControlScreen() {
    val context = LocalContext.current
    var petRunning by remember { mutableStateOf(false) } // This is a simple flag, could use a bound service later

    // Re-check permission (overlay) every time the screen is shown
    val overlayGranted = PermissionHelper.canDrawOverlays(context)

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Floating Pet", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (!overlayGranted) {
            Text("Overlay permission not granted. Please enable it in settings.",
                color = MaterialTheme.colorScheme.error)
            Button(onClick = { PermissionHelper.requestOverlayPermission(context) }) {
                Text("Open Overlay Settings")
            }
        } else {
            Button(
                onClick = {
                    if (!petRunning) {
                        // Check overlay again just in case
                        if (PermissionHelper.canDrawOverlays(context)) {
                            FloatingWindowService.start(context)
                            petRunning = true
                        } else {
                            Toast.makeText(context, "Overlay permission required.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        FloatingWindowService.stop(context)
                        petRunning = false
                    }
                }
            ) {
                Text(if (petRunning) "Stop Pet" else "Start Pet")
            }
        }
    }
}