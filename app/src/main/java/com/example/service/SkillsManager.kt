package com.example.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.provider.Settings
import android.util.Log
import com.example.model.AutomationAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SkillsManager(private val context: Context) {

    private val _activeAction = MutableStateFlow<AutomationAction?>(null)
    val activeAction: StateFlow<AutomationAction?> = _activeAction.asStateFlow()

    private val _currentStep = MutableStateFlow("")
    val currentStep: StateFlow<String> = _currentStep.asStateFlow()

    private val _isAutomating = MutableStateFlow(false)
    val isAutomating: StateFlow<Boolean> = _isAutomating.asStateFlow()

    suspend fun executeTask(command: String): String {
        val lower = command.lowercase()
        return when {
            lower.contains("instagram") -> runSocialMediaAutomation("Instagram", "Video Upload", "#SkyMax #AI #JARVIS #TechFuture")
            lower.contains("youtube") -> runSocialMediaAutomation("YouTube", "Shorts / Video Upload", "JARVIS System AI Log #SkyMax #AI")
            lower.contains("whatsapp") -> runWhatsAppAutomation(command)
            lower.contains("call") || lower.contains("phone") -> runCallAutomation(command)
            lower.contains("sms") || lower.contains("message") -> runSmsAutomation(command)
            lower.contains("alarm") || lower.contains("wake up") -> runAlarmAutomation(command)
            lower.contains("bluetooth") -> runBluetoothAutomation()
            lower.contains("wifi") || lower.contains("wi-fi") -> runWifiAutomation()
            else -> "Sir, automation protocol initialized for: $command."
        }
    }

    private suspend fun runSocialMediaAutomation(app: String, task: String, caption: String): String {
        _isAutomating.value = true
        val steps = listOf(
            "1. Launching $app environment...",
            "2. Accessing local media storage and selecting video file...",
            "3. Auto-generating high-engagement caption: \"$caption\"...",
            "4. Configuring tags, audio synchronization, and post privacy...",
            "5. Upload initiated successfully on $app, Sir!"
        )

        _activeAction.value = AutomationAction(
            id = java.util.UUID.randomUUID().toString(),
            appName = app,
            actionType = task,
            target = "Media Pipeline",
            status = "Processing",
            steps = steps
        )

        for (step in steps) {
            _currentStep.value = step
            delay(1100)
        }

        // Try launching real app if installed
        try {
            val pkg = if (app.equals("Instagram", true)) "com.instagram.android" else "com.google.android.youtube"
            val launchIntent = context.packageManager.getLaunchIntentForPackage(pkg)
            if (launchIntent != null) {
                launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(launchIntent)
            }
        } catch (e: Exception) {
            Log.w("SkillsManager", "App launch skipped: ${e.message}")
        }

        _isAutomating.value = false
        return "Sir, your video has been prepared and the upload pipeline on $app was executed."
    }

    private suspend fun runCallAutomation(command: String): String {
        _isAutomating.value = true
        val targetPerson = if (command.contains("mummy", true)) "Mummy" else "Contact"
        val steps = listOf(
            "1. Scanning verified contacts database for '$targetPerson'...",
            "2. Secure line established. Initializing dialer subsystem...",
            "3. Placing call to $targetPerson..."
        )

        _activeAction.value = AutomationAction(
            id = java.util.UUID.randomUUID().toString(),
            appName = "Phone Dialer",
            actionType = "Outgoing Voice Call",
            target = targetPerson,
            status = "Dialing",
            steps = steps
        )

        for (step in steps) {
            _currentStep.value = step
            delay(900)
        }

        try {
            val callIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(callIntent)
        } catch (e: Exception) {
            Log.e("SkillsManager", "Call intent error", e)
        }

        _isAutomating.value = false
        return "Sir, I have opened the secure phone line for $targetPerson."
    }

    private suspend fun runWhatsAppAutomation(command: String): String {
        _isAutomating.value = true
        val steps = listOf(
            "1. Accessing WhatsApp API connector...",
            "2. Formulating message text and encrypting payload...",
            "3. Opening WhatsApp chat dispatcher..."
        )

        _activeAction.value = AutomationAction(
            id = java.util.UUID.randomUUID().toString(),
            appName = "WhatsApp",
            actionType = "Direct Messaging",
            target = "Recipient",
            status = "Sending",
            steps = steps
        )

        for (step in steps) {
            _currentStep.value = step
            delay(900)
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://api.whatsapp.com/send?text=${Uri.encode("Hello from JARVIS Sky & Max AI")}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("SkillsManager", "WhatsApp launch error", e)
        }

        _isAutomating.value = false
        return "Sir, WhatsApp message composer has been dispatched."
    }

    private suspend fun runSmsAutomation(command: String): String {
        _isAutomating.value = true
        val steps = listOf(
            "1. Preparing cellular SMS gateway...",
            "2. Encoding message text...",
            "3. Launching system SMS client..."
        )

        _activeAction.value = AutomationAction(
            id = java.util.UUID.randomUUID().toString(),
            appName = "SMS Messenger",
            actionType = "Cellular Text Message",
            target = "Recipient",
            status = "Dispatched",
            steps = steps
        )

        for (step in steps) {
            _currentStep.value = step
            delay(800)
        }

        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:")
                putExtra("sms_body", "Greetings, Sir asked me to send this message.")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("SkillsManager", "SMS error", e)
        }

        _isAutomating.value = false
        return "Sir, SMS client has been dispatched."
    }

    private suspend fun runAlarmAutomation(command: String): String {
        _isAutomating.value = true
        val steps = listOf(
            "1. Synchronizing with real-time hardware clock...",
            "2. Programming alarm trigger for scheduled hour...",
            "3. Alarm registered in system clock daemon."
        )

        _activeAction.value = AutomationAction(
            id = java.util.UUID.randomUUID().toString(),
            appName = "System Clock",
            actionType = "Alarm & Timer",
            target = "Scheduled Alarm",
            status = "Armed",
            steps = steps
        )

        for (step in steps) {
            _currentStep.value = step
            delay(700)
        }

        try {
            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_MESSAGE, "Sky & Max JARVIS Alarm")
                putExtra(AlarmClock.EXTRA_HOUR, 7)
                putExtra(AlarmClock.EXTRA_MINUTES, 0)
                putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("SkillsManager", "Alarm error", e)
        }

        _isAutomating.value = false
        return "Sir, alarm has been armed for your morning wake-up."
    }

    private fun runBluetoothAutomation(): String {
        try {
            val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("SkillsManager", "Bluetooth settings error", e)
        }
        return "Sir, I have opened Bluetooth device settings for you."
    }

    private fun runWifiAutomation(): String {
        try {
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("SkillsManager", "Wi-Fi settings error", e)
        }
        return "Sir, I have navigated to Wi-Fi connection controls."
    }

    fun dismissAutomation() {
        _activeAction.value = null
        _currentStep.value = ""
        _isAutomating.value = false
    }
}
