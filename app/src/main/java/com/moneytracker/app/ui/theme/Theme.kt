package com.moneytracker.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = MoneyColors.TealDeep,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCCFBF1),
    onPrimaryContainer = MoneyColors.TealInk,
    secondary = Color(0xFF57534E),
    onSecondary = Color.White,
    secondaryContainer = MoneyColors.Mist,
    onSecondaryContainer = MoneyColors.Ink,
    background = MoneyColors.Sand,
    onBackground = MoneyColors.Ink,
    surface = Color(0xFFFFFCF8),
    onSurface = MoneyColors.Ink,
    surfaceVariant = MoneyColors.Mist,
    onSurfaceVariant = Color(0xFF5C5650),
    outline = Color(0xFFC4B8A8),
    error = MoneyColors.Coral,
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = MoneyColors.Teal,
    onPrimary = MoneyColors.TealInk,
    primaryContainer = MoneyColors.TealInk,
    onPrimaryContainer = Color(0xFFCCFBF1),
    background = Color(0xFF1C1917),
    onBackground = Color(0xFFFAFAF9),
    surface = Color(0xFF292524),
    onSurface = Color(0xFFFAFAF9),
    surfaceVariant = Color(0xFF44403C),
    onSurfaceVariant = Color(0xFFD6D3D1),
    error = Color(0xFFFDBA74),
)

@Composable
fun MoneyTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = MoneyTypography,
        content = content,
    )
}
