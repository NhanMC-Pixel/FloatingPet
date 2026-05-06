package com.floatingpet.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.floatingpet.app.ui.onboarding.OnboardingScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // If onboarding completed already, go straight to MainActivity
        if (isOnboardingComplete()) {
            startMainActivity()
            finish()
            return
        }
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                OnboardingScreen(
                    onOnboardingComplete = {
                        setOnboardingComplete()
                        startMainActivity()
                        finish()
                    }
                )
            }
        }
    }

    private fun isOnboardingComplete(): Boolean {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return prefs.getBoolean("onboarding_complete", false)
    }

    private fun setOnboardingComplete() {
        getSharedPreferences("app_prefs", MODE_PRIVATE)
            .edit()
            .putBoolean("onboarding_complete", true)
            .apply()
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}