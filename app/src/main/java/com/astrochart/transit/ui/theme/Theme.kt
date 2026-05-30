package com.astrochart.transit.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val EclipseBlack = Color(0xFF101113)
val PanelBlack = Color(0xFF18191C)
val LineGrey = Color(0xFF383B42)
val StarWhite = Color(0xFFF5F1E8)
val SolarGold = Color(0xFFF4C95D)
val TransitTeal = Color(0xFF54D6C2)
val MarsRose = Color(0xFFF76F8E)
val Verdant = Color(0xFF8FD694)

private val AstroColorScheme: ColorScheme = darkColorScheme(
    primary = SolarGold,
    onPrimary = EclipseBlack,
    secondary = TransitTeal,
    onSecondary = EclipseBlack,
    tertiary = MarsRose,
    background = EclipseBlack,
    onBackground = StarWhite,
    surface = PanelBlack,
    onSurface = StarWhite,
    surfaceVariant = Color(0xFF222429),
    onSurfaceVariant = Color(0xFFC9C3B6),
    outline = LineGrey,
    error = MarsRose
)

private val AstroShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(8.dp),
    extraLarge = RoundedCornerShape(8.dp)
)

@Composable
fun AstroTransitTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AstroColorScheme,
        shapes = AstroShapes,
        content = content
    )
}
