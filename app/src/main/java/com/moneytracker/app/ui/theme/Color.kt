package com.moneytracker.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object MoneyColors {
    val Teal = Color(0xFF0D9488)
    val TealDeep = Color(0xFF0F766E)
    val TealInk = Color(0xFF134E4A)
    val Sand = Color(0xFFF7F3EE)
    val SandDeep = Color(0xFFEDE6DC)
    val Ink = Color(0xFF1A1A1A)
    val Mist = Color(0xFFE8E2D9)
    val Coral = Color(0xFFC2410C)

    val ScreenBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFECFDF5),
            Sand,
            SandDeep,
        ),
    )

    val HeroBrush = Brush.linearGradient(
        colors = listOf(
            TealDeep,
            Color(0xFF0F766E),
            Color(0xFF115E59),
        ),
    )
}
