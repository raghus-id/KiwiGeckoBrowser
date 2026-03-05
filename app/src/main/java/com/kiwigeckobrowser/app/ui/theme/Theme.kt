package com.kiwigeckobrowser.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Kiwi-inspired sleek dark theme colors
val KiwiBackground = Color(0xFF121212)
val KiwiSurface = Color(0xFF1E1E1E)
val KiwiSurfaceVariant = Color(0xFF2C2C2C)
val KiwiAccent = Color(0xFF4285F4) // Google Blue
val KiwiOnSurface = Color(0xFFE0E0E0)
val KiwiOnSurfaceVariant = Color(0xFFA0A0A0)
val KiwiOutline = Color(0xFF333333)

private val DarkColorScheme = darkColorScheme(
    primary = KiwiAccent,
    secondary = KiwiAccent,
    tertiary = KiwiAccent,
    background = KiwiBackground,
    surface = KiwiSurface,
    surfaceVariant = KiwiSurfaceVariant,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = KiwiOnSurface,
    onSurface = KiwiOnSurface,
    onSurfaceVariant = KiwiOnSurfaceVariant,
    outline = KiwiOutline
)

@Composable
fun KiwiGeckoBrowserTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
