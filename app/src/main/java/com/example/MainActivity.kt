package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.service.AssistantEngine
import com.example.service.MediaHub
import com.example.service.MediaStudio
import com.example.service.MemoryManager
import com.example.service.SkillsManager
import com.example.service.SpeechManager
import com.example.service.UpdaterManager
import com.example.ui.screens.JarvisMainScreen
import com.example.ui.screens.PermissionsScreen
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private lateinit var speechManager: SpeechManager
    private lateinit var memoryManager: MemoryManager
    private lateinit var skillsManager: SkillsManager
    private lateinit var updaterManager: UpdaterManager
    private lateinit var mediaHub: MediaHub
    private lateinit var mediaStudio: MediaStudio
    private lateinit var assistantEngine: AssistantEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences("sky_max_app", Context.MODE_PRIVATE)
        val initialPermissionsCleared = prefs.getBoolean("permissions_initialized", false)

        speechManager = SpeechManager(applicationContext)
        memoryManager = MemoryManager(applicationContext)
        skillsManager = SkillsManager(applicationContext)
        updaterManager = UpdaterManager(applicationContext)
        mediaHub = MediaHub(applicationContext)
        mediaStudio = MediaStudio()

        assistantEngine = AssistantEngine(
            context = applicationContext,
            memoryManager = memoryManager,
            skillsManager = skillsManager,
            speechManager = speechManager
        )

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = JarvisBackground
                ) {
                    var permissionsCleared by remember { mutableStateOf(initialPermissionsCleared) }

                    if (!permissionsCleared) {
                        PermissionsScreen(
                            onPermissionsCleared = {
                                prefs.edit().putBoolean("permissions_initialized", true).apply()
                                permissionsCleared = true
                            }
                        )
                    } else {
                        JarvisMainScreen(
                            assistantEngine = assistantEngine,
                            speechManager = speechManager,
                            updaterManager = updaterManager,
                            mediaHub = mediaHub,
                            mediaStudio = mediaStudio,
                            skillsManager = skillsManager,
                            memoryManager = memoryManager
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::speechManager.isInitialized) {
            speechManager.release()
        }
        if (::mediaHub.isInitialized) {
            mediaHub.stopFm()
        }
    }
}
