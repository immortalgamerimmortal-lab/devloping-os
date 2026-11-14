package dev.serein.prototype.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Ink = Color(0xFF18252D)
private val Paper = Color(0xFFF4F6F5)
private val Night = Color(0xFF11191F)
private val Mist = Color(0xFFE1E9E7)

@Composable
fun SereinTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accent: Color,
    highContrast: Boolean,
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = accent,
            onPrimary = Color(0xFF102126),
            secondary = Color(0xFFB4CDC9),
            background = Night,
            onBackground = Color(0xFFF0F4F2),
            surface = Color(0xFF1B272E),
            onSurface = Color(0xFFF0F4F2),
            surfaceVariant = Color(0xFF27353C),
            onSurfaceVariant = Color(0xFFD4DEDA),
            outline = Color(0xFF92A29F),
        )
    } else {
        lightColorScheme(
            primary = accent,
            onPrimary = Color.White,
            secondary = Color(0xFF486B69),
            background = Paper,
            onBackground = Ink,
            surface = Color.White,
            onSurface = Ink,
            surfaceVariant = Mist,
            onSurfaceVariant = Color(0xFF35464B),
            outline = Color(0xFF617579),
        )
    }
    val accessibleColors = if (highContrast) {
        colors.copy(
            onBackground = if (darkTheme) Color.White else Color.Black,
            onSurface = if (darkTheme) Color.White else Color.Black,
            onSurfaceVariant = if (darkTheme) Color.White else Color(0xFF18252D),
            outline = if (darkTheme) Color.White else Color(0xFF26383C),
        )
    } else colors

    MaterialTheme(colorScheme = accessibleColors, content = content)
}
