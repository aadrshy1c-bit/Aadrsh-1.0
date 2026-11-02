package com.example.model

enum class AssistantOwner {
    SKY,
    MAX,
    BOTH
}

enum class MessageSender {
    USER,
    SKY,
    MAX,
    SYSTEM
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val language: String = "hi",
    val isSpoken: Boolean = false,
    val actionDetail: String? = null
)

data class MemoryItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val owner: AssistantOwner,
    val category: String, // e.g. "location", "preference", "habit", "search"
    val title: String,
    val detail: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class ReminderItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val targetTime: String,
    val owner: AssistantOwner,
    val caringTone: String,
    val humorAdvice: String,
    val isCompleted: Boolean = false,
    val isSpoken: Boolean = false
)

data class GitHubRelease(
    val tagName: String,
    val name: String,
    val releaseNotes: String,
    val apkDownloadUrl: String,
    val apkSizeMb: Double,
    val publishedAt: String,
    val isNewer: Boolean = false
)

data class RadioStation(
    val id: String,
    val frequency: String,
    val name: String,
    val genre: String,
    val isFmBand: Boolean = true
)

data class TvChannel(
    val id: String,
    val name: String,
    val category: String,
    val quality: String = "1080p FHD",
    val description: String
)

data class AutomationAction(
    val id: String,
    val appName: String,
    val actionType: String,
    val target: String,
    val status: String,
    val steps: List<String>
)
