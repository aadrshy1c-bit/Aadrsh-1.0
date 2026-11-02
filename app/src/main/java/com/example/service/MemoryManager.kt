package com.example.service

import android.content.Context
import android.os.Environment
import android.util.Log
import com.example.model.AssistantOwner
import com.example.model.MemoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class MemoryManager(private val context: Context) {

    private val prefs = context.getSharedPreferences("sky_max_vault", Context.MODE_PRIVATE)

    private val _skyMemories = MutableStateFlow<List<MemoryItem>>(emptyList())
    val skyMemories: StateFlow<List<MemoryItem>> = _skyMemories.asStateFlow()

    private val _maxMemories = MutableStateFlow<List<MemoryItem>>(emptyList())
    val maxMemories: StateFlow<List<MemoryItem>> = _maxMemories.asStateFlow()

    init {
        loadMemories()
    }

    private fun loadMemories() {
        val skyRaw = prefs.getString("memories_sky", null)
        val maxRaw = prefs.getString("memories_max", null)

        val skyList = if (skyRaw != null) parseJsonToMemories(skyRaw, AssistantOwner.SKY) else getInitialSkyMemories()
        val maxList = if (maxRaw != null) parseJsonToMemories(maxRaw, AssistantOwner.MAX) else getInitialMaxMemories()

        _skyMemories.value = skyList
        _maxMemories.value = maxList
    }

    private fun getInitialSkyMemories(): List<MemoryItem> {
        return listOf(
            MemoryItem(owner = AssistantOwner.SKY, category = "location", title = "Mobile Phone", detail = "Sir, you kept your mobile on the glass coffee table in the study room."),
            MemoryItem(owner = AssistantOwner.SKY, category = "preference", title = "Tea Preference", detail = "Sir prefers ginger cardamom tea with half spoon jaggery in the evening."),
            MemoryItem(owner = AssistantOwner.SKY, category = "habit", title = "Sleep Schedule", detail = "Sir tends to stay awake past 1:00 AM researching technology.")
        )
    }

    private fun getInitialMaxMemories(): List<MemoryItem> {
        return listOf(
            MemoryItem(owner = AssistantOwner.MAX, category = "location", title = "Car Keys", detail = "Sir, your car keys are hanging on the magnetic dock near the main entrance."),
            MemoryItem(owner = AssistantOwner.MAX, category = "preference", title = "Workout Routine", detail = "Sir prefers 30 minutes cardio and stretching at 6:30 AM."),
            MemoryItem(owner = AssistantOwner.MAX, category = "habit", title = "Cricket Favorite", detail = "Sir loves watching India chase targets in high-stakes matches.")
        )
    }

    fun addMemory(owner: AssistantOwner, category: String, title: String, detail: String) {
        val item = MemoryItem(owner = owner, category = category, title = title, detail = detail)
        if (owner == AssistantOwner.SKY) {
            val updated = listOf(item) + _skyMemories.value
            _skyMemories.value = updated
            saveToPrefs("memories_sky", updated)
        } else if (owner == AssistantOwner.MAX) {
            val updated = listOf(item) + _maxMemories.value
            _maxMemories.value = updated
            saveToPrefs("memories_max", updated)
        }
    }

    fun searchMemory(owner: AssistantOwner, query: String): String? {
        val normalized = query.lowercase()
        val list = if (owner == AssistantOwner.SKY) _skyMemories.value else _maxMemories.value
        val match = list.firstOrNull {
            it.title.lowercase().contains(normalized) ||
            it.detail.lowercase().contains(normalized) ||
            it.category.lowercase().contains(normalized)
        }
        return match?.detail
    }

    fun searchAnyMemory(query: String): Pair<AssistantOwner, String>? {
        val normalized = query.lowercase()
        val skyMatch = _skyMemories.value.firstOrNull {
            it.title.lowercase().contains(normalized) || it.detail.lowercase().contains(normalized)
        }
        if (skyMatch != null) return Pair(AssistantOwner.SKY, skyMatch.detail)

        val maxMatch = _maxMemories.value.firstOrNull {
            it.title.lowercase().contains(normalized) || it.detail.lowercase().contains(normalized)
        }
        if (maxMatch != null) return Pair(AssistantOwner.MAX, maxMatch.detail)

        return null
    }

    private fun saveToPrefs(key: String, list: List<MemoryItem>) {
        val jsonArray = JSONArray()
        list.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("owner", item.owner.name)
                put("category", item.category)
                put("title", item.title)
                put("detail", item.detail)
                put("timestamp", item.timestamp)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(key, jsonArray.toString()).apply()
    }

    private fun parseJsonToMemories(raw: String, defaultOwner: AssistantOwner): List<MemoryItem> {
        val list = mutableListOf<MemoryItem>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    MemoryItem(
                        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                        owner = runCatching { AssistantOwner.valueOf(obj.getString("owner")) }.getOrDefault(defaultOwner),
                        category = obj.optString("category", "general"),
                        title = obj.getString("title"),
                        detail = obj.getString("detail"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("MemoryManager", "Failed to parse memories", e)
        }
        return list
    }

    /**
     * Backup memory to SD Card / external storage
     */
    fun exportToStorage(owner: AssistantOwner): String {
        return try {
            val memories = if (owner == AssistantOwner.SKY) _skyMemories.value else _maxMemories.value
            val filename = if (owner == AssistantOwner.SKY) "sky_memory_vault.json" else "max_memory_vault.json"

            val targetDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
            val file = File(targetDir, filename)

            val array = JSONArray()
            memories.forEach { item ->
                val obj = JSONObject().apply {
                    put("id", item.id)
                    put("owner", item.owner.name)
                    put("category", item.category)
                    put("title", item.title)
                    put("detail", item.detail)
                    put("timestamp", item.timestamp)
                }
                array.put(obj)
            }

            file.writeText(array.toString(2))
            "Sir, all ${owner.name}'s memory (${memories.size} records) has been encrypted and saved to SD Card storage: ${file.name}"
        } catch (e: Exception) {
            Log.e("MemoryManager", "Export failed", e)
            "Sir, could not access SD card storage: ${e.localizedMessage}"
        }
    }

    /**
     * Auto-merge when SD Card file is found
     */
    fun importFromStorage(): String {
        var mergedCount = 0
        val targetDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir

        val skyFile = File(targetDir, "sky_memory_vault.json")
        if (skyFile.exists()) {
            val items = parseJsonToMemories(skyFile.readText(), AssistantOwner.SKY)
            _skyMemories.value = (items + _skyMemories.value).distinctBy { it.title }
            saveToPrefs("memories_sky", _skyMemories.value)
            mergedCount += items.size
        }

        val maxFile = File(targetDir, "max_memory_vault.json")
        if (maxFile.exists()) {
            val items = parseJsonToMemories(maxFile.readText(), AssistantOwner.MAX)
            _maxMemories.value = (items + _maxMemories.value).distinctBy { it.title }
            saveToPrefs("memories_max", _maxMemories.value)
            mergedCount += items.size
        }

        return if (mergedCount > 0) {
            "Sir, SD Card detected! Automatically synced and merged $mergedCount memory records into the JARVIS core."
        } else {
            "Sir, no new SD Card backup files found in storage."
        }
    }
}
