package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = PerlaGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = PerlaGreenLight,
    onPrimaryContainer = PerlaGreenDark,
    secondary = PerlaGreenSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC8E6C9),
    onSecondaryContainer = Color(0xFF003300),
    tertiary = PerlaGoldAccent,
    background = Color(0xFFF9FBF9),
    surface = Color.White,
    onSurface = Color(0xFF1B1D1B),
    surfaceVariant = PerlaSurfaceGreen,
    error = StatusInjured
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color(0xFF003300),
    primaryContainer = PerlaGreenDark,
    onPrimaryContainer = Color(0xFFA5D6A7),
    secondary = Color(0xFFA5D6A7),
    background = Color(0xFF121412),
    surface = Color(0xFF1E221E),
    onSurface = Color(0xFFE2E3E2)
)

@Composable
fun PerlaVerdeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
