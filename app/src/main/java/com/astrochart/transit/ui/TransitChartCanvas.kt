package com.astrochart.transit.ui

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import com.astrochart.transit.model.Aspect
import com.astrochart.transit.model.AspectType
import com.astrochart.transit.model.ChartState
import com.astrochart.transit.model.PlanetPosition
import com.astrochart.transit.model.ZodiacSign
import com.astrochart.transit.ui.theme.EclipseBlack
import com.astrochart.transit.ui.theme.LineGrey
import com.astrochart.transit.ui.theme.MarsRose
import com.astrochart.transit.ui.theme.SolarGold
import com.astrochart.transit.ui.theme.StarWhite
import com.astrochart.transit.ui.theme.TransitTeal
import com.astrochart.transit.ui.theme.Verdant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun TransitChartCanvas(
    chartState: ChartState,
    modifier: Modifier = Modifier
) {
    val timeFormatter = remember {
        DateTimeFormatter.ofPattern("MMM d HH:mm 'UTC'").withZone(ZoneOffset.UTC)
    }

    Canvas(modifier = modifier) {
        drawRect(EclipseBlack)

        val side = min(size.width, size.height)
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = side * 0.46f

        drawStarfield(center, radius)
        drawZodiacWheel(center, radius)
        drawAspectWeb(chartState.aspects, center, radius)
        drawPlanetRing(
            positions = chartState.natalPositions,
            center = center,
            ringRadius = radius * 0.56f,
            color = SolarGold,
            label = "Natal"
        )
        drawPlanetRing(
            positions = chartState.transitPositions,
            center = center,
            ringRadius = radius * 0.78f,
            color = TransitTeal,
            label = "Transit"
        )

        drawCircle(
            color = EclipseBlack,
            radius = radius * 0.26f,
            center = center
        )
        drawCircle(
            color = LineGrey.copy(alpha = 0.9f),
            radius = radius * 0.26f,
            center = center,
            style = Stroke(width = 1.2f)
        )
        drawNativeText(
            text = "TRANSIT",
            center = center + Offset(0f, -radius * 0.045f),
            textSize = side * 0.029f,
            color = TransitTeal,
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        )
        drawNativeText(
            text = timeFormatter.format(chartState.transitInstant),
            center = center + Offset(0f, radius * 0.04f),
            textSize = side * 0.026f,
            color = StarWhite.copy(alpha = 0.88f)
        )
    }
}

private fun DrawScope.drawStarfield(center: Offset, radius: Float) {
    for (index in 0 until 90) {
        val angle = (index * 137.507764) % 360.0
        val distance = radius * (0.18f + ((index * 37) % 100) / 118f)
        val point = polar(center, distance, angle)
        val alpha = 0.10f + ((index % 7) * 0.035f)
        drawCircle(
            color = StarWhite.copy(alpha = alpha.coerceAtMost(0.32f)),
            radius = if (index % 11 == 0) 1.8f else 1.0f,
            center = point
        )
    }
}

private fun DrawScope.drawZodiacWheel(center: Offset, radius: Float) {
    val bounds = Rect(
        left = center.x - radius,
        top = center.y - radius,
        right = center.x + radius,
        bottom = center.y + radius
    )
    val signColors = listOf(
        SolarGold,
        TransitTeal,
        MarsRose,
        Verdant,
        Color(0xFFE9E2A8),
        Color(0xFFA9D4FF)
    )

    ZodiacSign.entries.forEachIndexed { index, sign ->
        val color = signColors[index % signColors.size]
        drawArc(
            color = color.copy(alpha = 0.38f),
            startAngle = index * 30f - 90f,
            sweepAngle = 30f,
            useCenter = false,
            topLeft = bounds.topLeft,
            size = bounds.size,
            style = Stroke(width = radius * 0.09f)
        )
        drawLine(
            color = LineGrey.copy(alpha = 0.85f),
            start = polar(center, radius * 0.42f, index * 30.0),
            end = polar(center, radius, index * 30.0),
            strokeWidth = 1.1f
        )
        val glyphPoint = polar(center, radius * 0.91f, index * 30.0 + 15.0)
        drawNativeText(
            text = sign.glyph,
            center = glyphPoint + Offset(0f, radius * 0.018f),
            textSize = radius * 0.105f,
            color = color.copy(alpha = 0.94f),
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        )
    }

    listOf(1f, 0.86f, 0.68f, 0.43f).forEachIndexed { index, scale ->
        drawCircle(
            color = if (index == 0) StarWhite.copy(alpha = 0.48f) else LineGrey.copy(alpha = 0.74f),
            radius = radius * scale,
            center = center,
            style = Stroke(width = if (index == 0) 2.0f else 1.0f)
        )
    }

    for (degree in 0 until 360 step 5) {
        val major = degree % 30 == 0
        val start = polar(center, radius * if (major) 0.965f else 0.985f, degree.toDouble())
        val end = polar(center, radius, degree.toDouble())
        drawLine(
            color = StarWhite.copy(alpha = if (major) 0.62f else 0.22f),
            start = start,
            end = end,
            strokeWidth = if (major) 1.3f else 0.7f
        )
    }
}

private fun DrawScope.drawAspectWeb(
    aspects: List<Aspect>,
    center: Offset,
    radius: Float
) {
    aspects.take(28).forEach { aspect ->
        val color = aspectColor(aspect.type)
        val natal = polar(center, radius * 0.49f, aspect.natal.zodiacDegree)
        val transit = polar(center, radius * 0.70f, aspect.transit.zodiacDegree)
        drawLine(
            color = color.copy(alpha = 0.12f + aspect.strength * 0.56f),
            start = natal,
            end = transit,
            strokeWidth = 0.7f + aspect.strength * 2.1f
        )
    }
}

private fun DrawScope.drawPlanetRing(
    positions: List<PlanetPosition>,
    center: Offset,
    ringRadius: Float,
    color: Color,
    label: String
) {
    drawCircle(
        color = color.copy(alpha = 0.18f),
        radius = ringRadius,
        center = center,
        style = Stroke(width = 2.0f)
    )
    drawNativeText(
        text = label.uppercase(),
        center = center + Offset(0f, -ringRadius - 9f),
        textSize = 12f,
        color = color.copy(alpha = 0.82f),
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    )

    layoutGlyphs(positions).forEach { placement ->
        val point = polar(center, ringRadius + placement.radialOffset, placement.angle)
        drawCircle(
            color = EclipseBlack.copy(alpha = 0.96f),
            radius = 18f,
            center = point
        )
        drawCircle(
            color = color.copy(alpha = 0.52f),
            radius = 18f,
            center = point,
            style = Stroke(width = 1.5f)
        )
        drawNativeText(
            text = placement.position.celestialBody.glyph,
            center = point + Offset(0f, 7f),
            textSize = 24f,
            color = color,
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        )
        if (placement.position.isRetrograde) {
            drawNativeText(
                text = "R",
                center = point + Offset(15f, -12f),
                textSize = 9f,
                color = MarsRose,
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            )
        }
    }
}

private data class GlyphPlacement(
    val position: PlanetPosition,
    val angle: Double,
    val radialOffset: Float
)

private fun layoutGlyphs(positions: List<PlanetPosition>): List<GlyphPlacement> {
    if (positions.isEmpty()) return emptyList()
    val sorted = positions.sortedBy { it.zodiacDegree }
    var collisionDepth = 0

    return sorted.mapIndexed { index, position ->
        val previous = sorted[(index - 1 + sorted.size) % sorted.size]
        val delta = if (index == 0) {
            position.zodiacDegree + 360.0 - previous.zodiacDegree
        } else {
            position.zodiacDegree - previous.zodiacDegree
        }
        collisionDepth = if (delta <= 5.0) collisionDepth + 1 else 0
        val direction = if (index % 2 == 0) 1.0 else -1.0
        GlyphPlacement(
            position = position,
            angle = position.zodiacDegree + direction * min(collisionDepth * 1.4, 4.2),
            radialOffset = (collisionDepth * 12f).coerceAtMost(36f)
        )
    }
}

private fun DrawScope.drawNativeText(
    text: String,
    center: Offset,
    textSize: Float,
    color: Color,
    typeface: Typeface = Typeface.DEFAULT
) {
    drawContext.canvas.nativeCanvas.drawText(
        text,
        center.x,
        center.y,
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color.toArgb()
            this.textAlign = Paint.Align.CENTER
            this.textSize = textSize
            this.typeface = typeface
        }
    )
}

private fun Color.toArgb(): Int {
    val a = (alpha * 255).roundToInt().coerceIn(0, 255)
    val r = (red * 255).roundToInt().coerceIn(0, 255)
    val g = (green * 255).roundToInt().coerceIn(0, 255)
    val b = (blue * 255).roundToInt().coerceIn(0, 255)
    return (a shl 24) or (r shl 16) or (g shl 8) or b
}

private fun polar(center: Offset, radius: Float, degree: Double): Offset {
    // Mirror horizontally by negating the angle offset.
    val radians = (90.0 - degree) * PI / 180.0
    return Offset(
        x = center.x + cos(radians).toFloat() * radius,
        y = center.y + sin(radians).toFloat() * radius
    )
}

private fun aspectColor(type: AspectType): Color =
    when (type) {
        AspectType.Conjunction -> SolarGold
        AspectType.Square -> MarsRose
        AspectType.Trine -> Verdant
        AspectType.Opposition -> TransitTeal
    }
