package com.astrochart.transit.model

import java.time.Instant
import kotlin.math.floor

enum class CelestialBody(
    val displayName: String,
    val glyph: String,
    val shortCode: String
) {
    Sun("Sun", "☉", "SUN"),
    Moon("Moon", "☽", "MON"),
    Mercury("Mercury", "☿", "MER"),
    Venus("Venus", "♀", "VEN"),
    Mars("Mars", "♂", "MAR"),
    Jupiter("Jupiter", "♃", "JUP"),
    Saturn("Saturn", "♄", "SAT"),
    Uranus("Uranus", "♅", "URA"),
    Neptune("Neptune", "♆", "NEP"),
    Pluto("Pluto", "♇", "PLU");
}

enum class ZodiacSign(val label: String, val glyph: String) {
    Aries("Aries", "♈"),
    Taurus("Taurus", "♉"),
    Gemini("Gemini", "♊"),
    Cancer("Cancer", "♋"),
    Leo("Leo", "♌"),
    Virgo("Virgo", "♍"),
    Libra("Libra", "♎"),
    Scorpio("Scorpio", "♏"),
    Sagittarius("Sagittarius", "♐"),
    Capricorn("Capricorn", "♑"),
    Aquarius("Aquarius", "♒"),
    Pisces("Pisces", "♓");

    companion object {
        fun fromLongitude(longitude: Double): ZodiacSign {
            val normalized = ((longitude % 360.0) + 360.0) % 360.0
            return entries[floor(normalized / 30.0).toInt().coerceIn(0, 11)]
        }
    }
}

enum class ChartRing {
    Natal,
    Transit
}

data class PlanetPosition(
    val celestialBody: CelestialBody,
    val zodiacDegree: Double,
    val zodiacSign: ZodiacSign,
    val degreeInSign: Double,
    val isRetrograde: Boolean,
    val speedDegreesPerDay: Double,
    val ring: ChartRing
) {
    val formattedDegree: String
        get() = "${degreeInSign.toInt()}°${((degreeInSign % 1.0) * 60.0).toInt().toString().padStart(2, '0')}′ ${zodiacSign.glyph}"
}

enum class AspectType(
    val title: String,
    val angle: Double,
    val glyph: String
) {
    Conjunction("Conjunction", 0.0, "☌"),
    Square("Square", 90.0, "□"),
    Trine("Trine", 120.0, "△"),
    Opposition("Opposition", 180.0, "☍");
}

data class Aspect(
    val natal: PlanetPosition,
    val transit: PlanetPosition,
    val type: AspectType,
    val orb: Double,
    val strength: Float
)

data class ObserverLocation(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

data class ChartState(
    val natalInstant: Instant,
    val transitInstant: Instant,
    val natalPositions: List<PlanetPosition>,
    val transitPositions: List<PlanetPosition>,
    val aspects: List<Aspect>,
    val orbTolerance: Double,
    val source: String
) {
    companion object {
        val Empty = ChartState(
            natalInstant = Instant.EPOCH,
            transitInstant = Instant.EPOCH,
            natalPositions = emptyList(),
            transitPositions = emptyList(),
            aspects = emptyList(),
            orbTolerance = 6.0,
            source = "Astronomy Engine"
        )
    }
}
