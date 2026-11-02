package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AssistantOwner
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisBorder
import com.example.ui.theme.JarvisBorderGlow
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisSurfaceElevated
import com.example.ui.theme.JarvisSurfaceVariant
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import com.example.ui.theme.MaxAmber
import com.example.ui.theme.SkyCyan

@Composable
fun ArcReactorCore(
    activeAssistant: AssistantOwner,
    isSpeaking: Boolean,
    isListening: Boolean,
    amplitude: Float,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "reactor")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val reverseRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "reverse_rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val primaryColor = when (activeAssistant) {
        AssistantOwner.SKY -> SkyCyan
        AssistantOwner.MAX -> MaxAmber
        AssistantOwner.BOTH -> SkyCyan
    }

    Box(
        modifier = modifier
            .size(160.dp)
            .clip(CircleShape)
            .clickable { onClick() }
            .testTag("arc_reactor_core"),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow & rotating rings
        Canvas(modifier = Modifier.size(150.dp).rotate(rotation)) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2 - 8f

            drawCircle(
                color = primaryColor.copy(alpha = 0.15f),
                radius = radius,
                style = Stroke(width = 3.dp.toPx())
            )

            // Tech ticks
            val tickCount = 12
            for (i in 0 until tickCount) {
                val angle = (i * 360f / tickCount) * (Math.PI / 180.0)
                val startX = center.x + (radius - 12f) * kotlin.math.cos(angle).toFloat()
                val startY = center.y + (radius - 12f) * kotlin.math.sin(angle).toFloat()
                val endX = center.x + radius * kotlin.math.cos(angle).toFloat()
                val endY = center.y + radius * kotlin.math.sin(angle).toFloat()
                drawLine(
                    color = primaryColor.copy(alpha = 0.7f),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }

        // Inner counter-rotating ring
        Canvas(modifier = Modifier.size(110.dp).rotate(reverseRotation)) {
            val center = Offset(size.width / 2, size.height / 2)
            val innerRadius = size.width / 2 - 6f
            drawCircle(
                color = primaryColor.copy(alpha = 0.4f),
                radius = innerRadius,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Central glowing reactor core
        Box(
            modifier = Modifier
                .size((74 * (if (isListening) (1f + amplitude * 0.3f) else pulseScale)).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            primaryColor,
                            primaryColor.copy(alpha = 0.7f),
                            primaryColor.copy(alpha = 0.1f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.Mic,
                contentDescription = "JARVIS Core Voice Indicator",
                tint = JarvisBackground,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun AudioWaveformVisualizer(
    isSpeaking: Boolean,
    isListening: Boolean,
    amplitude: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val animWave1 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(300, easing = LinearEasing), RepeatMode.Reverse),
        label = "w1"
    )
    val animWave2 by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(450, easing = LinearEasing), RepeatMode.Reverse),
        label = "w2"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val bars = 24
        for (i in 0 until bars) {
            val dynamicHeight = if (isListening) {
                (12 + (amplitude * 20f * (if (i % 2 == 0) animWave1 else animWave2))).coerceIn(4f, 30f)
            } else if (isSpeaking) {
                (8 + (22f * (if (i % 3 == 0) animWave1 else animWave2))).coerceIn(4f, 28f)
            } else {
                4f
            }

            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(dynamicHeight.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color.copy(alpha = if (isSpeaking || isListening) 0.85f else 0.3f))
            )
        }
    }
}

@Composable
fun JarvisGlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = JarvisBorder,
    glowColor: Color = JarvisBorderGlow,
    cornerRadius: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(cornerRadius)
            ),
        shape = RoundedCornerShape(cornerRadius),
        color = JarvisSurfaceVariant.copy(alpha = 0.85f),
        content = content
    )
}

@Composable
fun JarvisStatusPill(
    label: String,
    statusText: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(accentColor.copy(alpha = 0.12f))
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(accentColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$label: ",
            fontSize = 11.sp,
            color = JarvisTextSecondary,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = statusText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            fontFamily = FontFamily.Monospace
        )
    }
}
