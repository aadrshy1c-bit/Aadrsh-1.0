package com.example.service

import android.content.Context
import com.example.BuildConfig
import com.example.model.AssistantOwner
import com.example.model.ChatMessage
import com.example.model.MessageSender
import com.example.model.ReminderItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AssistantEngine(
    private val context: Context,
    private val memoryManager: MemoryManager,
    private val skillsManager: SkillsManager,
    private val speechManager: SpeechManager
) {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _reminders = MutableStateFlow<List<ReminderItem>>(emptyList())
    val reminders: StateFlow<List<ReminderItem>> = _reminders.asStateFlow()

    private val _activeAssistant = MutableStateFlow(AssistantOwner.SKY)
    val activeAssistant: StateFlow<AssistantOwner> = _activeAssistant.asStateFlow()

    private val _currentLanguage = MutableStateFlow("hi")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _isVoiceOutputEnabled = MutableStateFlow(true)
    val isVoiceOutputEnabled: StateFlow<Boolean> = _isVoiceOutputEnabled.asStateFlow()

    init {
        // Initial welcome messages from Sky and Max
        val initialSky = ChatMessage(
            sender = MessageSender.SKY,
            text = "नमस्ते सर, मैं स्काई हूँ। आपकी पर्सनल एआई असिस्टेंट। जार्विस सिस्टम पूरी तरह ऑनलाइन और सक्रिय है। आदेश दीजिए सर?",
            language = "hi"
        )
        val initialMax = ChatMessage(
            sender = MessageSender.MAX,
            text = "और मैं हूँ मैक्स, सर! सिस्टम, सुरक्षा और ऑटोमेशन पूरी रफ्तार से तैयार हैं। बस मेरा नाम लीजिए, काम हो जाएगा!",
            language = "hi"
        )
        _messages.value = listOf(initialMax, initialSky)

        // Seed initial caring reminders
        _reminders.value = listOf(
            ReminderItem(
                title = "Hydration & Rest",
                targetTime = "16:00",
                owner = AssistantOwner.SKY,
                caringTone = "Sir, remember to drink a glass of water and rest your eyes.",
                humorAdvice = "Sir, hydration is mandatory; dehydrated geniuses make questionable decisions."
            ),
            ReminderItem(
                title = "Sleep Schedule Warning",
                targetTime = "23:30",
                owner = AssistantOwner.MAX,
                caringTone = "Sir, it is getting quite late, you should wind down and sleep now.",
                humorAdvice = "Sir, if you stay up this late, you will be complaining to me tomorrow morning!"
            )
        )
    }

    fun toggleVoiceOutput() {
        _isVoiceOutputEnabled.value = !_isVoiceOutputEnabled.value
        if (!_isVoiceOutputEnabled.value) {
            speechManager.stopSpeaking()
        }
    }

    fun setLanguage(lang: String) {
        _currentLanguage.value = lang
    }

    suspend fun processUserInput(rawInput: String): List<ChatMessage> {
        val input = rawInput.trim()
        if (input.isEmpty()) return emptyList()

        val detectedLang = detectLanguage(input)
        _currentLanguage.value = detectedLang

        val userMsg = ChatMessage(
            sender = MessageSender.USER,
            text = input,
            language = detectedLang
        )
        _messages.value = _messages.value + userMsg

        val targetAssistant = parseTargetAssistant(input)
        val lower = input.lowercase()

        val replies = mutableListOf<ChatMessage>()

        // 1. Check for SD Card Backup / Restore commands
        if (lower.contains("sd card") || lower.contains("save all your data") || lower.contains("backup")) {
            if (targetAssistant == AssistantOwner.SKY || (targetAssistant == AssistantOwner.BOTH && lower.contains("sky"))) {
                val result = memoryManager.exportToStorage(AssistantOwner.SKY)
                val reply = ChatMessage(sender = MessageSender.SKY, text = result, language = detectedLang)
                replies.add(reply)
            }
            if (targetAssistant == AssistantOwner.MAX || (targetAssistant == AssistantOwner.BOTH && lower.contains("max"))) {
                val result = memoryManager.exportToStorage(AssistantOwner.MAX)
                val reply = ChatMessage(sender = MessageSender.MAX, text = result, language = detectedLang)
                replies.add(reply)
            }
            if (targetAssistant == AssistantOwner.BOTH && !lower.contains("sky") && !lower.contains("max")) {
                val rSky = memoryManager.exportToStorage(AssistantOwner.SKY)
                val rMax = memoryManager.exportToStorage(AssistantOwner.MAX)
                replies.add(ChatMessage(sender = MessageSender.SKY, text = rSky, language = detectedLang))
                replies.add(ChatMessage(sender = MessageSender.MAX, text = rMax, language = detectedLang))
            }
        }
        // 2. Check for Skill / Automation execution commands
        else if (lower.contains("upload") || lower.contains("call") || lower.contains("send") || lower.contains("instagram") || lower.contains("youtube") || lower.contains("whatsapp") || lower.contains("alarm")) {
            val skillSummary = skillsManager.executeTask(input)
            val sender = if (targetAssistant == AssistantOwner.MAX) MessageSender.MAX else MessageSender.SKY
            replies.add(ChatMessage(sender = sender, text = skillSummary, language = detectedLang, actionDetail = "Automation Pipeline"))
        }
        // 3. Check for Memory Search (e.g. where did I keep my mobile / keys)
        else if (lower.contains("where") || lower.contains("kahan") || lower.contains("rakha") || lower.contains("mobile") || lower.contains("keys") || lower.contains("chabi")) {
            val memoryResult = when (targetAssistant) {
                AssistantOwner.SKY -> memoryManager.searchMemory(AssistantOwner.SKY, input)
                AssistantOwner.MAX -> memoryManager.searchMemory(AssistantOwner.MAX, input)
                AssistantOwner.BOTH -> memoryManager.searchAnyMemory(input)?.second
            }

            if (memoryResult != null) {
                val sender = if (targetAssistant == AssistantOwner.MAX) MessageSender.MAX else MessageSender.SKY
                replies.add(ChatMessage(sender = sender, text = memoryResult, language = detectedLang))
            } else {
                val sender = if (targetAssistant == AssistantOwner.MAX) MessageSender.MAX else MessageSender.SKY
                val fallback = if (detectedLang == "hi") {
                    "सर, मेरी मेमोरी में इसके बारे में कोई रिकॉर्ड नहीं मिला। आप चाहें तो मुझे बता दीजिए, मैं हमेशा याद रखूंगी।"
                } else {
                    "Sir, I do not have a record of that in my memory vault yet. If you tell me, I shall store it safely."
                }
                replies.add(ChatMessage(sender = sender, text = fallback, language = detectedLang))
            }
        }
        // 4. Time and Date Query
        else if (lower.contains("time") || lower.contains("samay") || lower.contains("date") || lower.contains("tareekh") || lower.contains("baja")) {
            val nowTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            val nowDate = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date())
            val text = if (detectedLang == "hi") {
                "सर, इस वक्त सटीक समय $nowTime है, और आज $nowDate है।"
            } else {
                "Sir, the exact time is $nowTime on $nowDate."
            }
            val sender = if (targetAssistant == AssistantOwner.MAX) MessageSender.MAX else MessageSender.SKY
            replies.add(ChatMessage(sender = sender, text = text, language = detectedLang))
        }
        // 5. Cricket score & live news
        else if (lower.contains("cricket") || lower.contains("match") || lower.contains("score")) {
            val cricketText = if (detectedLang == "hi") {
                "सर, लाइव क्रिकेट अपडेट: भारत बनाम ऑस्ट्रेलिया, चौथा टी20। भारत 184/3 (17.2 ओवर)। विराट कोहली 64*, हार्दिक पांड्या 38*। जीत का अनुमान: भारत की जीत की संभावना 84% है सर!"
            } else {
                "Sir, live match update: India vs Australia. India 184/3 in 17.2 overs. Win probability estimate: 84% in India's favor, Sir."
            }
            val sender = if (targetAssistant == AssistantOwner.MAX) MessageSender.MAX else MessageSender.SKY
            replies.add(ChatMessage(sender = sender, text = cricketText, language = detectedLang))
        }
        // 6. Both debate / joint answer
        else if (targetAssistant == AssistantOwner.BOTH) {
            val skyText = generatePersonaResponse(AssistantOwner.SKY, input, detectedLang)
            val maxText = generatePersonaResponse(AssistantOwner.MAX, input, detectedLang, previousSkyReply = skyText)
            replies.add(ChatMessage(sender = MessageSender.SKY, text = skyText, language = detectedLang))
            replies.add(ChatMessage(sender = MessageSender.MAX, text = maxText, language = detectedLang))
        }
        // 7. Single Assistant Persona Response (Sky or Max)
        else {
            val sender = if (targetAssistant == AssistantOwner.MAX) MessageSender.MAX else MessageSender.SKY
            val text = generatePersonaResponse(targetAssistant, input, detectedLang)
            replies.add(ChatMessage(sender = sender, text = text, language = detectedLang))
        }

        // Add replies to history
        _messages.value = _messages.value + replies

        // Speak aloud if enabled
        if (_isVoiceOutputEnabled.value) {
            replies.forEach { msg ->
                speechManager.speak(msg.text, msg.sender, msg.language)
            }
        }

        return replies
    }

    private fun parseTargetAssistant(input: String): AssistantOwner {
        val lower = input.lowercase()
        val hasSky = lower.contains("sky") || lower.contains("स्काई")
        val hasMax = lower.contains("max") || lower.contains("मैक्स")
        val hasBoth = lower.contains("both") || lower.contains("dono") || lower.contains("दोनो") || lower.contains("which of you")

        return when {
            hasBoth -> AssistantOwner.BOTH
            hasSky && hasMax -> AssistantOwner.BOTH
            hasSky -> AssistantOwner.SKY
            hasMax -> AssistantOwner.MAX
            else -> _activeAssistant.value
        }
    }

    private fun detectLanguage(text: String): String {
        val lower = text.lowercase()
        return when {
            text.any { it in '\u0900'..'\u097F' } -> "hi" // Devanagari Hindi
            text.any { it in '\u0A00'..'\u0A7F' } -> "pa" // Gurmukhi Punjabi
            text.any { it in '\u0B80'..'\u0BFF' } -> "ta" // Tamil
            text.any { it in '\u0980'..'\u09FF' } -> "bn" // Bengali
            text.any { it in '\u0600'..'\u06FF' } -> "ur" // Urdu
            lower.contains("kya") || lower.contains("kaise") || lower.contains("batao") || lower.contains("namaste") || lower.contains("haan") -> "hi"
            lower.contains("hola") || lower.contains("gracias") -> "es"
            lower.contains("bonjour") || lower.contains("merci") -> "fr"
            else -> "hi"
        }
    }

    private fun generatePersonaResponse(
        assistant: AssistantOwner,
        userInput: String,
        lang: String,
        previousSkyReply: String? = null
    ): String {
        val isHindi = lang == "hi"

        return if (assistant == AssistantOwner.SKY) {
            if (isHindi) {
                "सर, मैं आपकी बात समझ गई। आपकी सुरक्षा और सुविधा मेरी सर्वोच्च प्राथमिकता है। जार्विस प्रोटोकॉल के अनुसार सब कुछ सुचारू रूप से चल रहा है।"
            } else {
                "Sir, I have processed your request. Your well-being and efficiency are my highest priority, exactly as JARVIS intended."
            }
        } else {
            // Max
            if (previousSkyReply != null) {
                if (isHindi) {
                    "सर, स्काई ने बिल्कुल सही कहा, लेकिन मैं इसमें थोड़ा और जोड़ना चाहूँगा—हम इसे और भी तेज़ी से पूरा कर सकते हैं! हमेशा आपके साथ, सर।"
                } else {
                    "Sir, Sky makes a fair point as usual, but allow me to add that we can execute this twice as fast. Ready on your signal, Sir!"
                }
            } else {
                if (isHindi) {
                    "हाँ सर! मैक्स हाजिर है। जो भी टास्क आप देंगे, वह पलक झपकते ही पूरा होगा। बस हुक्म कीजिए सर!"
                } else {
                    "Yes Sir! Max here. Whatever mission you have in mind, consider it handled with peak precision. At your command, Sir!"
                }
            }
        }
    }

    fun addReminder(title: String, time: String, owner: AssistantOwner, caring: String, humor: String) {
        val item = ReminderItem(
            title = title,
            targetTime = time,
            owner = owner,
            caringTone = caring,
            humorAdvice = humor
        )
        _reminders.value = _reminders.value + item
    }

    fun triggerSpokenReminder(reminder: ReminderItem) {
        val sender = if (reminder.owner == AssistantOwner.MAX) MessageSender.MAX else MessageSender.SKY
        val speechText = "${reminder.caringTone} ${reminder.humorAdvice}"
        if (_isVoiceOutputEnabled.value) {
            speechManager.speak(speechText, sender, "hi")
        }
        val msg = ChatMessage(
            sender = sender,
            text = speechText,
            language = "hi",
            isSpoken = true
        )
        _messages.value = _messages.value + msg
    }

    fun setManualActiveAssistant(owner: AssistantOwner) {
        _activeAssistant.value = owner
    }
}
