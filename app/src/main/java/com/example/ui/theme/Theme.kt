package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    primaryContainer = CyberPurple,
    onPrimaryContainer = Color.White,
    secondary = CyberPurple,
    onSecondary = Color.White,
    secondaryContainer = CharcoalSurfaceVariant,
    onSecondaryContainer = TextPrimary,
    tertiary = EmeraldGreen,
    onTertiary = Color.Black,
    background = ObsidianBackground,
    onBackground = TextPrimary,
    surface = CharcoalSurface,
    onSurface = TextPrimary,
    surfaceVariant = CharcoalSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = CharcoalSurfaceVariant,
    outlineVariant = CharcoalSurfaceVariant
)

private val LightColorScheme = lightColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    background = Color.White,
    surface = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun FriendHubTheme(
    darkTheme: Boolean = true,
    fontName: String = "Plus Jakarta Sans",
    content: @Composable () -> Unit
) {
    val selectedFont = if (fontName == "Default" || fontName.isBlank()) "Plus Jakarta Sans" else fontName
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = getTypographyForFont(selectedFont),
        content = content
    )
}

