package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AssistantOwner
import com.example.model.ChatMessage
import com.example.model.MessageSender
import com.example.service.AssistantEngine
import com.example.service.MediaHub
import com.example.service.MediaStudio
import com.example.service.MemoryManager
import com.example.service.SkillsManager
import com.example.service.SpeechManager
import com.example.service.UpdaterManager
import com.example.ui.components.ArcReactorCore
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.JarvisGlassCard
import com.example.ui.components.JarvisStatusPill
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisBorder
import com.example.ui.theme.JarvisBorderGlow
import com.example.ui.theme.JarvisError
import com.example.ui.theme.JarvisInfo
import com.example.ui.theme.JarvisSuccess
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisSurfaceElevated
import com.example.ui.theme.JarvisSurfaceVariant
import com.example.ui.theme.JarvisTextMuted
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import com.example.ui.theme.MaxAmber
import com.example.ui.theme.SkyCyan
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JarvisMainScreen(
    assistantEngine: AssistantEngine,
    speechManager: SpeechManager,
    updaterManager: UpdaterManager,
    mediaHub: MediaHub,
    mediaStudio: MediaStudio,
    skillsManager: SkillsManager,
    memoryManager: MemoryManager
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) }
    var textInput by remember { mutableStateOf("") }
    var showUpdaterModal by remember { mutableStateOf(false) }

    val messages by assistantEngine.messages.collectAsState()
    val activeAssistant by assistantEngine.activeAssistant.collectAsState()
    val isVoiceOutputEnabled by assistantEngine.isVoiceOutputEnabled.collectAsState()
    val isListening by speechManager.isListening.collectAsState()
    val isSpeaking by speechManager.isSpeaking.collectAsState()
    val speechAmplitude by speechManager.speechAmplitude.collectAsState()
    val availableRelease by updaterManager.availableRelease.collectAsState()
    val activeAction by skillsManager.activeAction.collectAsState()
    val currentStep by skillsManager.currentStep.collectAsState()

    var currentTimeString by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        // Initial GitHub release check
        updaterManager.checkForUpdates()
        while (true) {
            currentTimeString = SimpleDateFormat("HH:mm:ss • EEE, dd MMM", Locale.getDefault()).format(Date())
            delay(1000)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("jarvis_main_screen"),
        containerColor = JarvisBackground,
        bottomBar = {
            NavigationBar(
                containerColor = JarvisSurface,
                modifier = Modifier.border(1.dp, JarvisBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                val navItems = listOf(
                    Triple("Console", Icons.Default.Chat, 0),
                    Triple("Reminders", Icons.Default.Alarm, 1),
                    Triple("Media FM/TV", Icons.Default.Radio, 2),
                    Triple("Studio", Icons.Default.AutoAwesome, 3),
                    Triple("Skills", Icons.Default.Bolt, 4),
                    Triple("Updater", Icons.Default.Settings, 5)
                )
                navItems.forEach { (label, icon, index) ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(imageVector = icon, contentDescription = label) },
                        label = { Text(label, fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SkyCyan,
                            selectedTextColor = SkyCyan,
                            unselectedIconColor = JarvisTextMuted,
                            unselectedTextColor = JarvisTextMuted,
                            indicatorColor = SkyCyan.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_tab_$index")
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // HUD Top Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, JarvisBorder, RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp)),
                color = JarvisSurface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(SkyCyan)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SKY & MAX",
                                color = SkyCyan,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "JARVIS v${updaterManager.currentVersion.collectAsState().value}",
                                color = MaxAmber,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = currentTimeString,
                            color = JarvisTextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Voice audio toggle
                        IconButton(
                            onClick = { assistantEngine.toggleVoiceOutput() },
                            modifier = Modifier.size(34.dp).testTag("toggle_voice_output_button")
                        ) {
                            Icon(
                                imageVector = if (isVoiceOutputEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                contentDescription = "Voice Output",
                                tint = if (isVoiceOutputEnabled) SkyCyan else JarvisTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Assistant selector toggle
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (activeAssistant == AssistantOwner.SKY) SkyCyan.copy(alpha = 0.2f) else MaxAmber.copy(alpha = 0.2f))
                                .border(1.dp, if (activeAssistant == AssistantOwner.SKY) SkyCyan else MaxAmber, RoundedCornerShape(8.dp))
                                .clickable {
                                    val next = if (activeAssistant == AssistantOwner.SKY) AssistantOwner.MAX else AssistantOwner.SKY
                                    assistantEngine.setManualActiveAssistant(next)
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("active_assistant_badge")
                        ) {
                            Text(
                                text = if (activeAssistant == AssistantOwner.SKY) "SKY (F)" else "MAX (M)",
                                color = if (activeAssistant == AssistantOwner.SKY) SkyCyan else MaxAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // In-app updater alert banner if available
            availableRelease?.let { rel ->
                UpdateNotificationBanner(
                    release = rel,
                    onOpenDialog = { showUpdaterModal = true },
                    onDismiss = { updaterManager.dismissRelease() }
                )
            }

            // Live automation execution overlay if active
            AnimatedVisibility(
                visible = activeAction != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                activeAction?.let { act ->
                    JarvisGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        borderColor = MaxAmber
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(MaxAmber)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "AUTOMATION: ${act.appName.uppercase()}",
                                        color = MaxAmber,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Text(
                                    text = act.status,
                                    color = SkyCyan,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentStep,
                                color = JarvisTextPrimary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Tab Content
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (selectedTab) {
                    0 -> ConsoleChatTab(
                        assistantEngine = assistantEngine,
                        speechManager = speechManager,
                        messages = messages,
                        activeAssistant = activeAssistant,
                        isSpeaking = isSpeaking,
                        isListening = isListening,
                        amplitude = speechAmplitude,
                        textInput = textInput,
                        onTextInputChange = { textInput = it },
                        onSendText = {
                            if (textInput.isNotBlank()) {
                                coroutineScope.launch {
                                    assistantEngine.processUserInput(textInput)
                                    textInput = ""
                                }
                            }
                        }
                    )
                    1 -> RemindersTab(assistantEngine = assistantEngine)
                    2 -> MediaHubTab(mediaHub = mediaHub)
                    3 -> MediaStudioTab(mediaStudio = mediaStudio)
                    4 -> SkillsTab(skillsManager = skillsManager, assistantEngine = assistantEngine)
                    5 -> UpdaterAndSettingsTab(updaterManager = updaterManager, memoryManager = memoryManager)
                }
            }
        }
    }

    if (showUpdaterModal && availableRelease != null) {
        InAppUpdaterModal(
            updaterManager = updaterManager,
            release = availableRelease!!,
            onDismiss = { showUpdaterModal = false }
        )
    }
}

// ---------------------- TAB 1: CONSOLE CHAT ----------------------
@Composable
fun ConsoleChatTab(
    assistantEngine: AssistantEngine,
    speechManager: SpeechManager,
    messages: List<ChatMessage>,
    activeAssistant: AssistantOwner,
    isSpeaking: Boolean,
    isListening: Boolean,
    amplitude: Float,
    textInput: String,
    onTextInputChange: (String) -> Unit,
    onSendText: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val quickPrompts = listOf(
        "Sky, set a reminder for me",
        "Max, check live cricket score",
        "Sky, save all your data to this SD card",
        "Max, upload this video to Instagram",
        "Both of you tell me, what should I cook?",
        "Where did I keep my mobile?",
        "Max, tune the radio to 98.3"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Arc Reactor HUD Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            ArcReactorCore(
                activeAssistant = activeAssistant,
                isSpeaking = isSpeaking,
                isListening = isListening,
                amplitude = amplitude,
                onClick = {
                    if (isListening) {
                        speechManager.stopListening()
                    } else {
                        speechManager.startListening { voiceResult ->
                            coroutineScope.launch {
                                assistantEngine.processUserInput(voiceResult)
                            }
                        }
                    }
                }
            )
        }

        // Live Audio Frequency Waves
        AudioWaveformVisualizer(
            isSpeaking = isSpeaking,
            isListening = isListening,
            amplitude = amplitude,
            color = if (activeAssistant == AssistantOwner.SKY) SkyCyan else MaxAmber
        )

        // Status line
        Text(
            text = if (isListening) "LISTENING TO SIR..." else if (isSpeaking) "SPEAKING AS ${activeAssistant.name}..." else "WAKE WORD READY • SAY 'SKY' OR 'MAX'",
            color = if (isListening) SkyCyan else if (isSpeaking) MaxAmber else JarvisTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp),
            textAlign = TextAlign.Center
        )

        // Quick Suggestion Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickPrompts) { prompt ->
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            coroutineScope.launch {
                                assistantEngine.processUserInput(prompt)
                            }
                        }
                        .border(1.dp, JarvisBorder, RoundedCornerShape(12.dp)),
                    color = JarvisSurfaceVariant.copy(alpha = 0.7f)
                ) {
                    Text(
                        text = prompt,
                        color = JarvisTextPrimary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Conversation Feed
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages.reversed()) { msg ->
                MessageBubble(msg)
            }
        }

        // Input bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, JarvisBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            color = JarvisSurface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Voice Mic Button
                IconButton(
                    onClick = {
                        if (isListening) {
                            speechManager.stopListening()
                        } else {
                            speechManager.startListening { voiceResult ->
                                coroutineScope.launch {
                                    assistantEngine.processUserInput(voiceResult)
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (isListening) SkyCyan else JarvisSurfaceElevated)
                        .border(1.dp, if (isListening) SkyCyan else JarvisBorder, CircleShape)
                        .testTag("voice_input_mic_button")
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Mic else Icons.Default.Mic,
                        contentDescription = "Voice Input",
                        tint = if (isListening) JarvisBackground else SkyCyan
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = textInput,
                    onValueChange = onTextInputChange,
                    placeholder = {
                        Text(
                            "Address 'Sky' or 'Max'...",
                            color = JarvisTextMuted,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_textfield"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SkyCyan,
                        unfocusedBorderColor = JarvisBorder,
                        focusedTextColor = JarvisTextPrimary,
                        unfocusedTextColor = JarvisTextPrimary,
                        focusedContainerColor = JarvisSurfaceElevated,
                        unfocusedContainerColor = JarvisSurfaceElevated
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSendText() })
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onSendText,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SkyCyan)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = JarvisBackground,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MessageBubble(msg: ChatMessage) {
    val isUser = msg.sender == MessageSender.USER
    val isSky = msg.sender == MessageSender.SKY
    val isMax = msg.sender == MessageSender.MAX

    val bubbleBorder = when {
        isSky -> SkyCyan
        isMax -> MaxAmber
        isUser -> JarvisBorder
        else -> JarvisInfo
    }

    val bubbleBackground = when {
        isSky -> SkyCyan.copy(alpha = 0.08f)
        isMax -> MaxAmber.copy(alpha = 0.08f)
        isUser -> JarvisSurfaceElevated
        else -> JarvisSurfaceVariant
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!isUser) {
                Text(
                    text = if (isSky) "SKY (AI)" else "MAX (AI)",
                    color = if (isSky) SkyCyan else MaxAmber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            } else {
                Text(
                    text = "SIR",
                    color = JarvisTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(msg.timestamp)),
                color = JarvisTextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(if (isUser) 0.85f else 0.95f)
                .clip(RoundedCornerShape(14.dp))
                .background(bubbleBackground)
                .border(1.dp, bubbleBorder.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Text(
                text = msg.text,
                color = JarvisTextPrimary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

// ---------------------- TAB 2: REMINDERS & CARING ----------------------
@Composable
fun RemindersTab(assistantEngine: AssistantEngine) {
    val reminders by assistantEngine.reminders.collectAsState()
    var newTitle by remember { mutableStateOf("") }
    var newTime by remember { mutableStateOf("20:00") }
    var selectedOwner by remember { mutableStateOf(AssistantOwner.SKY) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "JARVIS CARING REMINDERS & ALARMS",
                color = SkyCyan,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "Sir, Sky & Max watch over your rest, meetings, nutrition, and well-being with humorous advice.",
                color = JarvisTextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )
        }

        items(reminders) { item ->
            JarvisGlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (item.owner == AssistantOwner.SKY) SkyCyan else MaxAmber
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.title,
                            color = JarvisTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (item.owner == AssistantOwner.SKY) SkyCyan.copy(alpha = 0.15f) else MaxAmber.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${item.owner.name} • ${item.targetTime}",
                                color = if (item.owner == AssistantOwner.SKY) SkyCyan else MaxAmber,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "• Care: \"${item.caringTone}\"",
                        color = JarvisTextSecondary,
                        fontSize = 12.sp
                    )

                    Text(
                        text = "• Advice: \"${item.humorAdvice}\"",
                        color = if (item.owner == AssistantOwner.SKY) SkyCyan else MaxAmber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { assistantEngine.triggerSpokenReminder(item) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (item.owner == AssistantOwner.SKY) SkyCyan else MaxAmber,
                                contentColor = JarvisBackground
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Speak Aloud", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            JarvisGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ADD CUSTOM REMINDER",
                        color = SkyCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        placeholder = { Text("Meeting / Dinner / Sleep...", color = JarvisTextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SkyCyan,
                            unfocusedBorderColor = JarvisBorder,
                            focusedTextColor = JarvisTextPrimary,
                            unfocusedTextColor = JarvisTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            FilterChip(
                                selected = selectedOwner == AssistantOwner.SKY,
                                onClick = { selectedOwner = AssistantOwner.SKY },
                                label = { Text("Sky") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SkyCyan,
                                    selectedLabelColor = JarvisBackground
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            FilterChip(
                                selected = selectedOwner == AssistantOwner.MAX,
                                onClick = { selectedOwner = AssistantOwner.MAX },
                                label = { Text("Max") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaxAmber,
                                    selectedLabelColor = JarvisBackground
                                )
                            )
                        }

                        Button(
                            onClick = {
                                if (newTitle.isNotBlank()) {
                                    assistantEngine.addReminder(
                                        title = newTitle,
                                        time = newTime,
                                        owner = selectedOwner,
                                        caring = "Sir, remember to attend to $newTitle.",
                                        humor = "Sir, punctuality is the hallmark of a true visionary."
                                    )
                                    newTitle = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SkyCyan,
                                contentColor = JarvisBackground
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Add Reminder", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ---------------------- TAB 3: MEDIA FM RADIO & LIVE TV ----------------------
@Composable
fun MediaHubTab(mediaHub: MediaHub) {
    val isFmPlaying by mediaHub.isFmPlaying.collectAsState()
    val frequency by mediaHub.currentFrequency.collectAsState()
    val isEarphoneConnected by mediaHub.isEarphoneAntennaConnected.collectAsState()
    val isBluetooth by mediaHub.isBluetoothOutput.collectAsState()
    val volume by mediaHub.radioVolume.collectAsState()

    val isTvPlaying by mediaHub.isTvPlaying.collectAsState()
    val selectedChannel by mediaHub.selectedTvChannel.collectAsState()
    val tvSource by mediaHub.tvSourceMode.collectAsState()
    val tvMessage by mediaHub.tvStatusMessage.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- FM RADIO SECTION ---
        item {
            Text(
                text = "FM RADIO SYNTHESIZER (87.5 - 108.0 MHz)",
                color = SkyCyan,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "Works offline with device FM hardware or synthesized internet fallback. Supports earphones antenna & Bluetooth audio.",
                color = JarvisTextSecondary,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
            )

            JarvisGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Big frequency display
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${String.format("%.1f", frequency)} MHz",
                                color = SkyCyan,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = if (isFmPlaying) "BROADCAST ACTIVE • STEREO" else "RADIO OFF",
                                color = if (isFmPlaying) JarvisSuccess else JarvisTextMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        IconButton(
                            onClick = {
                                if (isFmPlaying) mediaHub.stopFm() else mediaHub.playFm()
                            },
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (isFmPlaying) JarvisError else SkyCyan)
                                .testTag("fm_play_pause_button")
                        ) {
                            Icon(
                                imageVector = if (isFmPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = "Play/Stop",
                                tint = JarvisBackground,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Frequency slider
                    Slider(
                        value = frequency,
                        onValueChange = { mediaHub.setFrequency(it) },
                        valueRange = 87.5f..108.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = SkyCyan,
                            activeTrackColor = SkyCyan,
                            inactiveTrackColor = JarvisSurfaceElevated
                        ),
                        modifier = Modifier.testTag("fm_frequency_slider")
                    )

                    // Hardware Antenna & Bluetooth toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilterChip(
                            selected = isEarphoneConnected,
                            onClick = { mediaHub.toggleEarphoneAntenna() },
                            label = { Text(if (isEarphoneConnected) "Earphone Antenna: OK" else "Internal Antenna") },
                            leadingIcon = { Icon(Icons.Default.Headphones, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SkyCyan.copy(alpha = 0.2f),
                                selectedLabelColor = SkyCyan
                            )
                        )

                        FilterChip(
                            selected = isBluetooth,
                            onClick = { mediaHub.toggleBluetoothOutput() },
                            label = { Text(if (isBluetooth) "BT Output: ON" else "Speaker") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaxAmber.copy(alpha = 0.2f),
                                selectedLabelColor = MaxAmber
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Radio station presets
                    Text(
                        text = "PRESET STATIONS:",
                        color = JarvisTextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(mediaHub.radioPresets) { station ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { mediaHub.tunePreset(station) }
                                    .border(1.dp, if (station.frequency.toFloatOrNull() == frequency) SkyCyan else JarvisBorder, RoundedCornerShape(8.dp)),
                                color = JarvisSurfaceElevated
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                    Text(
                                        text = "${station.frequency} ${station.name}",
                                        color = if (station.frequency.toFloatOrNull() == frequency) SkyCyan else JarvisTextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(text = station.genre, color = JarvisTextMuted, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- LIVE TV SECTION ---
        item {
            Text(
                text = "LIVE TV CHANNELS (FREE DISH & INTERNET)",
                color = MaxAmber,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "Watch TV with OTG TV Receiver, Wi-Fi Free Dish Set-top Box, or Internet stream.",
                color = JarvisTextSecondary,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
            )

            JarvisGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Simulated TV Screen display
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black)
                            .border(1.dp, MaxAmber.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isTvPlaying) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Tv, contentDescription = null, tint = MaxAmber, modifier = Modifier.size(36.dp))
                                Text(
                                    text = selectedChannel?.name ?: "Live Channel",
                                    color = JarvisTextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "LIVE STREAMING • 1080p FHD 60FPS",
                                    color = JarvisSuccess,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Tv, contentDescription = null, tint = JarvisTextMuted, modifier = Modifier.size(36.dp))
                                Text("TV In Standby, Sir", color = JarvisTextMuted, fontSize = 13.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = tvMessage.ifEmpty { "Sir, select a channel or input source below." },
                        color = JarvisTextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // TV Channels selector
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(mediaHub.tvChannels) { ch ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { mediaHub.playTvChannel(ch) }
                                    .border(1.dp, if (selectedChannel?.id == ch.id) MaxAmber else JarvisBorder, RoundedCornerShape(8.dp)),
                                color = JarvisSurfaceElevated
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                    Text(
                                        text = ch.name,
                                        color = if (selectedChannel?.id == ch.id) MaxAmber else JarvisTextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(text = ch.category, color = JarvisTextMuted, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------------- TAB 4: AI HOLOGRAM STUDIO ----------------------
@Composable
fun MediaStudioTab(mediaStudio: MediaStudio) {
    val coroutineScope = rememberCoroutineScope()
    var prompt by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf("Holographic Futuristic") }
    var videoDurationMinutes by remember { mutableIntStateOf(10) }

    val isGenImage by mediaStudio.isGeneratingImage.collectAsState()
    val isGenVideo by mediaStudio.isGeneratingVideo.collectAsState()
    val videoProgress by mediaStudio.videoGenerationProgress.collectAsState()
    val videoStatus by mediaStudio.videoGenerationStatus.collectAsState()
    val images by mediaStudio.generatedImages.collectAsState()
    val videos by mediaStudio.generatedVideos.collectAsState()

    val styles = listOf("Holographic Futuristic", "Photorealistic 4K", "Cyberpunk", "Anime", "Oil Painting")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "JARVIS AI IMAGE & VIDEO GENERATOR",
                color = SkyCyan,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "Voice & text generative studio: Text-to-Image, 4K Upscale, Text-to-Video, Long Stitched Videos (10m, 15m, 1h), and Photo Editing.",
                color = JarvisTextSecondary,
                fontSize = 12.sp
            )
        }

        item {
            JarvisGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "GENERATE AI ART & 4K UPSCALE",
                        color = SkyCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { prompt = it },
                        placeholder = { Text("Describe image or video in full detail...", color = JarvisTextMuted) },
                        modifier = Modifier.fillMaxWidth().testTag("studio_prompt_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SkyCyan,
                            unfocusedBorderColor = JarvisBorder,
                            focusedTextColor = JarvisTextPrimary,
                            unfocusedTextColor = JarvisTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(styles) { st ->
                            FilterChip(
                                selected = selectedStyle == st,
                                onClick = { selectedStyle = st },
                                label = { Text(st, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SkyCyan,
                                    selectedLabelColor = JarvisBackground
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (prompt.isNotBlank()) {
                                    coroutineScope.launch {
                                        mediaStudio.generateImageFromPrompt(prompt, selectedStyle)
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f).testTag("generate_image_button"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SkyCyan,
                                contentColor = JarvisBackground
                            )
                        ) {
                            Text(if (isGenImage) "Rendering..." else "Generate 4K Image", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                if (prompt.isNotBlank()) {
                                    coroutineScope.launch {
                                        mediaStudio.generateVideo(prompt, videoDurationMinutes)
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f).testTag("generate_video_button"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaxAmber,
                                contentColor = JarvisBackground
                            )
                        ) {
                            Text(if (isGenVideo) "Stitching..." else "Make AI Video", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (isGenVideo) {
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { videoProgress },
                            modifier = Modifier.fillMaxWidth().height(6.dp),
                            color = MaxAmber
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = videoStatus, color = JarvisTextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        item {
            Text(
                text = "GALLERY & VIDEO TIMELINES",
                color = JarvisTextSecondary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        items(images) { art ->
            JarvisGlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Brush.radialGradient(listOf(SkyCyan.copy(alpha = 0.5f), JarvisSurfaceElevated))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = SkyCyan)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = art.prompt, color = JarvisTextPrimary, fontSize = 13.sp, maxLines = 2)
                        Text(text = "Style: ${art.style} • 4K Upscaled", color = SkyCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        items(videos) { vid ->
            JarvisGlassCard(modifier = Modifier.fillMaxWidth(), borderColor = MaxAmber) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaxAmber.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Movie, contentDescription = null, tint = MaxAmber)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = vid.prompt, color = JarvisTextPrimary, fontSize = 13.sp, maxLines = 2)
                        Text(text = "Length: ${vid.durationText} (${vid.segmentCount} segments) • ${vid.resolution}", color = MaxAmber, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

// ---------------------- TAB 5: SKILLS & AUTOMATIONS ----------------------
@Composable
fun SkillsTab(skillsManager: SkillsManager, assistantEngine: AssistantEngine) {
    val coroutineScope = rememberCoroutineScope()
    val skillsList = listOf(
        Pair("Instagram Video Upload", "Auto-selects video, crafts tags & caption, opens Instagram upload."),
        Pair("YouTube Shorts Dispatch", "Builds short video metadata and prepares YouTube pipeline."),
        Pair("Call Mummy", "Connects to telephone dialer subsystem with direct phone call intent."),
        Pair("WhatsApp Automated Messaging", "Formats encrypted text message and launches WhatsApp."),
        Pair("System Alarm Clock", "Arms hardware alarm clock for morning wake-up schedule."),
        Pair("Bluetooth Radio Controller", "Navigates to hardware Bluetooth audio and device pairing.")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "JARVIS SYSTEM AUTOMATION SKILLS",
                color = SkyCyan,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "Sir, speak commands like 'Max, upload this video to Instagram' or 'Sky, call Mummy'. All tasks execute live with on-screen visual telemetry.",
                color = JarvisTextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
            )
        }

        items(skillsList) { (title, desc) ->
            JarvisGlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = title, color = JarvisTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = desc, color = JarvisTextSecondary, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                skillsManager.executeTask(title)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SkyCyan,
                            contentColor = JarvisBackground
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Execute", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ---------------------- TAB 6: UPDATER & SETTINGS ----------------------
@Composable
fun UpdaterAndSettingsTab(updaterManager: UpdaterManager, memoryManager: MemoryManager) {
    val coroutineScope = rememberCoroutineScope()
    val owner by updaterManager.repoOwner.collectAsState()
    val repo by updaterManager.repoName.collectAsState()
    val currentVer by updaterManager.currentVersion.collectAsState()
    val availableRel by updaterManager.availableRelease.collectAsState()
    val isChecking by updaterManager.isChecking.collectAsState()
    val statusText by updaterManager.downloadStatus.collectAsState()

    var inputOwner by remember { mutableStateOf(owner) }
    var inputRepo by remember { mutableStateOf(repo) }

    // Self-update prompt & password
    var promptUpdate by remember { mutableStateOf("") }
    var passwordUpdate by remember { mutableStateOf("") }
    val selfUpdateStatus by updaterManager.selfUpdateStatus.collectAsState()

    var sdCardStatus by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "IN-APP GITHUB UPDATER & REPOSITORY",
                color = SkyCyan,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "Checks GitHub Releases API. When a new version is detected, downloads the APK asset and triggers installation.",
                color = JarvisTextSecondary,
                fontSize = 12.sp
            )
        }

        item {
            JarvisGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Current App Version:", color = JarvisTextSecondary, fontSize = 13.sp)
                        Text(text = "v$currentVer", color = SkyCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Latest GitHub Release:", color = JarvisTextSecondary, fontSize = 13.sp)
                        Text(
                            text = availableRel?.tagName ?: "Up to date",
                            color = if (availableRel != null) MaxAmber else JarvisSuccess,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(text = "GitHub Repository (owner/repo):", color = JarvisTextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = inputOwner,
                            onValueChange = { inputOwner = it },
                            placeholder = { Text("owner") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SkyCyan,
                                unfocusedBorderColor = JarvisBorder,
                                focusedTextColor = JarvisTextPrimary,
                                unfocusedTextColor = JarvisTextPrimary
                            )
                        )
                        OutlinedTextField(
                            value = inputRepo,
                            onValueChange = { inputRepo = it },
                            placeholder = { Text("repo") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SkyCyan,
                                unfocusedBorderColor = JarvisBorder,
                                focusedTextColor = JarvisTextPrimary,
                                unfocusedTextColor = JarvisTextPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                updaterManager.setRepository(inputOwner, inputRepo)
                                coroutineScope.launch { updaterManager.checkForUpdates(forceSimulatedNewVersion = false) }
                            },
                            modifier = Modifier.weight(1f).testTag("check_updates_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SkyCyan,
                                contentColor = JarvisBackground
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isChecking) "Checking..." else "Check GitHub", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                coroutineScope.launch { updaterManager.checkForUpdates(forceSimulatedNewVersion = true) }
                            },
                            modifier = Modifier.weight(1f).testTag("simulate_release_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaxAmber,
                                contentColor = JarvisBackground
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Simulate Release", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (statusText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = statusText, color = JarvisTextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        // Section 11: Prompt & Password Self-Update Console
        item {
            Text(
                text = "PROMPT & PASSWORD SELF-UPDATE CONSOLE",
                color = MaxAmber,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "Provide code modification prompt and authorized security password. Verifies code stability before hot-patching.",
                color = JarvisTextSecondary,
                fontSize = 11.sp
            )

            JarvisGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    OutlinedTextField(
                        value = promptUpdate,
                        onValueChange = { promptUpdate = it },
                        placeholder = { Text("Enter prompt from external AI...", color = JarvisTextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaxAmber,
                            unfocusedBorderColor = JarvisBorder,
                            focusedTextColor = JarvisTextPrimary,
                            unfocusedTextColor = JarvisTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = passwordUpdate,
                        onValueChange = { passwordUpdate = it },
                        placeholder = { Text("Security Password (default: jarvis77)", color = JarvisTextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaxAmber,
                            unfocusedBorderColor = JarvisBorder,
                            focusedTextColor = JarvisTextPrimary,
                            unfocusedTextColor = JarvisTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                updaterManager.executeSelfUpdate(promptUpdate, passwordUpdate)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaxAmber,
                            contentColor = JarvisBackground
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("AUTHENTICATE & HOT-PATCH UPDATE", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }

                    if (selfUpdateStatus.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = selfUpdateStatus, color = JarvisTextPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        // Section 7: SD Card Memory Export & Merge
        item {
            Text(
                text = "SD CARD ENCRYPTED MEMORY VAULT",
                color = SkyCyan,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            JarvisGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Separate memory vaults for Sky and Max. Data is saved only when requested.",
                        color = JarvisTextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                sdCardStatus = memoryManager.exportToStorage(AssistantOwner.SKY)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SkyCyan,
                                contentColor = JarvisBackground
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Export Sky SD", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                sdCardStatus = memoryManager.exportToStorage(AssistantOwner.MAX)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaxAmber,
                                contentColor = JarvisBackground
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Export Max SD", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                sdCardStatus = memoryManager.importFromStorage()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = JarvisSurfaceElevated,
                                contentColor = JarvisTextPrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Auto-Merge", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (sdCardStatus.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = sdCardStatus, color = SkyCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}
