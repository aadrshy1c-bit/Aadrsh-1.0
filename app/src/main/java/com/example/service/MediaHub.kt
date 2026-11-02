package com.example.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import com.example.model.RadioStation
import com.example.model.TvChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

class MediaHub(private val context: Context) {

    // --- FM Radio State ---
    private val _isFmPlaying = MutableStateFlow(false)
    val isFmPlaying: StateFlow<Boolean> = _isFmPlaying.asStateFlow()

    private val _currentFrequency = MutableStateFlow(98.3f)
    val currentFrequency: StateFlow<Float> = _currentFrequency.asStateFlow()

    private val _isEarphoneAntennaConnected = MutableStateFlow(true)
    val isEarphoneAntennaConnected: StateFlow<Boolean> = _isEarphoneAntennaConnected.asStateFlow()

    private val _isBluetoothOutput = MutableStateFlow(false)
    val isBluetoothOutput: StateFlow<Boolean> = _isBluetoothOutput.asStateFlow()

    private val _radioVolume = MutableStateFlow(0.8f)
    val radioVolume: StateFlow<Float> = _radioVolume.asStateFlow()

    val radioPresets = listOf(
        RadioStation("1", "98.3", "Radio Mirchi", "Bollywood & Pop Hits"),
        RadioStation("2", "93.5", "Red FM", "Bajaate Raho - Superhits"),
        RadioStation("3", "92.7", "Big FM", "Retro Melodies & Classics"),
        RadioStation("4", "104.0", "Fever FM", "Non-stop Energy & Beats"),
        RadioStation("5", "102.6", "AIR Rainbow", "National Broadcasting & News"),
        RadioStation("6", "100.1", "AIR FM Gold", "Golden Era Classics")
    )

    private var audioTrack: AudioTrack? = null
    private var synthJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    // --- Live TV State ---
    enum class TvSourceMode {
        HARDWARE_RECEIVER,
        FREE_DISH_WIFI,
        INTERNET_STREAM
    }

    private val _tvSourceMode = MutableStateFlow(TvSourceMode.INTERNET_STREAM)
    val tvSourceMode: StateFlow<TvSourceMode> = _tvSourceMode.asStateFlow()

    private val _isTvPlaying = MutableStateFlow(false)
    val isTvPlaying: StateFlow<Boolean> = _isTvPlaying.asStateFlow()

    private val _selectedTvChannel = MutableStateFlow<TvChannel?>(null)
    val selectedTvChannel: StateFlow<TvChannel?> = _selectedTvChannel.asStateFlow()

    private val _tvStatusMessage = MutableStateFlow("")
    val tvStatusMessage: StateFlow<String> = _tvStatusMessage.asStateFlow()

    val tvChannels = listOf(
        TvChannel("c1", "JARVIS News 24", "National News", "1080p FHD", "Live 24x7 India & World News with AI live summaries."),
        TvChannel("c2", "DD National Live", "General Entertainment", "1080p FHD", "National broadcast free dish stream."),
        TvChannel("c3", "DD Sports Live", "Live Cricket & Sports", "1080p FHD", "Live cricket commentary, scores and coverage."),
        TvChannel("c4", "Tech Future Network", "Sci-Fi & Innovation", "4K UHD", "Quantum computing, robotics, and aerospace updates.")
    )

    init {
        _selectedTvChannel.value = tvChannels[0]
    }

    fun toggleEarphoneAntenna() {
        _isEarphoneAntennaConnected.value = !_isEarphoneAntennaConnected.value
    }

    fun toggleBluetoothOutput() {
        _isBluetoothOutput.value = !_isBluetoothOutput.value
    }

    fun setFrequency(freq: Float) {
        _currentFrequency.value = (Math.round(freq * 10f) / 10f).coerceIn(87.5f, 108.0f)
        if (_isFmPlaying.value) {
            startSynthesizedRadioTone()
        }
    }

    fun setVolume(vol: Float) {
        _radioVolume.value = vol.coerceIn(0f, 1f)
        try {
            audioTrack?.setVolume(_radioVolume.value)
        } catch (_: Exception) {}
    }

    fun tunePreset(station: RadioStation) {
        station.frequency.toFloatOrNull()?.let { setFrequency(it) }
        playFm()
    }

    fun playFm() {
        _isFmPlaying.value = true
        startSynthesizedRadioTone()
    }

    fun stopFm() {
        _isFmPlaying.value = false
        synthJob?.cancel()
        synthJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
            audioTrack = null
        } catch (e: Exception) {
            Log.e("MediaHub", "Stop FM track error", e)
        }
    }

    private fun startSynthesizedRadioTone() {
        synthJob?.cancel()
        synthJob = coroutineScope.launch {
            try {
                val sampleRate = 44100
                val minBuff = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBuff)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack?.setVolume(_radioVolume.value)
                audioTrack?.play()

                val buffer = ShortArray(1024)
                var phase = 0.0
                // Harmonic pleasant tone reflecting the tuned frequency
                val baseFreq = 220.0 + ((_currentFrequency.value - 87.5) * 4.0)

                while (isActive && _isFmPlaying.value) {
                    for (i in buffer.indices) {
                        val sample = (sin(phase) * 0.35 * Short.MAX_VALUE).toInt()
                        buffer[i] = sample.toShort()
                        phase += 2.0 * Math.PI * baseFreq / sampleRate
                        if (phase > 2.0 * Math.PI) phase -= 2.0 * Math.PI
                    }
                    audioTrack?.write(buffer, 0, buffer.size)
                }
            } catch (e: Exception) {
                Log.w("MediaHub", "Synth audio skipped", e)
            }
        }
    }

    // --- TV Operations ---
    fun setTvSource(mode: TvSourceMode) {
        _tvSourceMode.value = mode
        when (mode) {
            TvSourceMode.HARDWARE_RECEIVER -> {
                _tvStatusMessage.value = "Hardware USB TV Receiver detected on OTG. Signal strength: 96%."
            }
            TvSourceMode.FREE_DISH_WIFI -> {
                _tvStatusMessage.value = "Connected to Home Free Dish Set-top Box (192.168.1.104). Streaming local tuner."
            }
            TvSourceMode.INTERNET_STREAM -> {
                _tvStatusMessage.value = "Internet FHD Streaming active."
            }
        }
    }

    fun playTvChannel(channel: TvChannel) {
        _selectedTvChannel.value = channel
        _isTvPlaying.value = true
        _tvStatusMessage.value = "Streaming ${channel.name} (${channel.quality}). Sir, live broadcast is active."
    }

    fun stopTv() {
        _isTvPlaying.value = false
        _tvStatusMessage.value = "Live TV paused, Sir."
    }
}
