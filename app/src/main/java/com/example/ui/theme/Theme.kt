package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val JarvisDarkColorScheme = darkColorScheme(
    primary = SkyCyan,
    onPrimary = JarvisBackground,
    primaryContainer = SkyCyanDark,
    onPrimaryContainer = SkyCyan,
    secondary = MaxAmber,
    onSecondary = JarvisBackground,
    secondaryContainer = MaxAmberDark,
    onSecondaryContainer = MaxAmber,
    tertiary = JarvisInfo,
    onTertiary = JarvisBackground,
    background = JarvisBackground,
    onBackground = JarvisTextPrimary,
    surface = JarvisSurface,
    onSurface = JarvisTextPrimary,
    surfaceVariant = JarvisSurfaceVariant,
    onSurfaceVariant = JarvisTextSecondary,
    outline = JarvisBorder,
    error = JarvisError,
    onError = JarvisBackground
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = JarvisDarkColorScheme,
        typography = Typography,
        content = content
    )
}
