package com.astrochart.transit.engine

import com.astrochart.transit.model.Aspect
import com.astrochart.transit.model.AspectType
import com.astrochart.transit.model.CelestialBody
import com.astrochart.transit.model.ChartRing
import com.astrochart.transit.model.ChartState
import com.astrochart.transit.model.ObserverLocation
import com.astrochart.transit.model.PlanetPosition
import com.astrochart.transit.model.ZodiacSign
import io.github.cosinekitty.astronomy.Aberration
import io.github.cosinekitty.astronomy.Body as AstroBody
import io.github.cosinekitty.astronomy.Time
import io.github.cosinekitty.astronomy.eclipticGeoMoon
import io.github.cosinekitty.astronomy.equatorialToEcliptic
import io.github.cosinekitty.astronomy.geoVector
import io.github.cosinekitty.astronomy.sunPosition
import java.time.Instant
import java.time.ZoneOffset
import kotlin.math.abs
import kotlin.math.floor

interface CelestialCalculator {
    fun calculateChart(
        natalInstant: Instant,
        transitInstant: Instant,
        observerLocation: ObserverLocation,
        orbTolerance: Double
    ): ChartState
}

class AstronomyEngineCalculator : CelestialCalculator {
    override fun calculateChart(
        natalInstant: Instant,
        transitInstant: Instant,
        observerLocation: ObserverLocation,
        orbTolerance: Double
    ): ChartState {
        val natal = calculatePositions(natalInstant, ChartRing.Natal)
        val transit = calculatePositions(transitInstant, ChartRing.Transit)

        return ChartState(
            natalInstant = natalInstant,
            transitInstant = transitInstant,
            natalPositions = natal,
            transitPositions = transit,
            aspects = calculateAspects(natal, transit, orbTolerance),
            orbTolerance = orbTolerance,
            source = "Astronomy Engine 2.1.19, geocentric apparent ecliptic longitude"
        )
    }

    private fun calculatePositions(instant: Instant, ring: ChartRing): List<PlanetPosition> {
        val time = instant.toAstronomyTime()
        return CelestialBody.entries.map { body ->
            val longitude = longitudeFor(body, time)
            val previous = longitudeFor(body, time.addDays(-0.25))
            val next = longitudeFor(body, time.addDays(0.25))
            val speed = signedAngularDelta(previous, next) / 0.5
            val normalized = longitude.normalizedDegrees()
            val signIndex = floor(normalized / 30.0).toInt().coerceIn(0, 11)
            PlanetPosition(
                celestialBody = body,
                zodiacDegree = normalized,
                zodiacSign = ZodiacSign.entries[signIndex],
                degreeInSign = normalized - (signIndex * 30.0),
                isRetrograde = speed < -0.0001,
                speedDegreesPerDay = speed,
                ring = ring
            )
        }
    }

    private fun longitudeFor(body: CelestialBody, time: Time): Double =
        when (body) {
            CelestialBody.Sun -> sunPosition(time).elon
            CelestialBody.Moon -> eclipticGeoMoon(time).lon
            CelestialBody.Mercury -> planetLongitude(AstroBody.Mercury, time)
            CelestialBody.Venus -> planetLongitude(AstroBody.Venus, time)
            CelestialBody.Mars -> planetLongitude(AstroBody.Mars, time)
            CelestialBody.Jupiter -> planetLongitude(AstroBody.Jupiter, time)
            CelestialBody.Saturn -> planetLongitude(AstroBody.Saturn, time)
            CelestialBody.Uranus -> planetLongitude(AstroBody.Uranus, time)
            CelestialBody.Neptune -> planetLongitude(AstroBody.Neptune, time)
            CelestialBody.Pluto -> planetLongitude(AstroBody.Pluto, time)
        }.normalizedDegrees()

    private fun planetLongitude(body: AstroBody, time: Time): Double {
        val geocentric = geoVector(body, time, Aberration.Corrected)
        return equatorialToEcliptic(geocentric).elon
    }

    private fun calculateAspects(
        natal: List<PlanetPosition>,
        transit: List<PlanetPosition>,
        orbTolerance: Double
    ): List<Aspect> {
        val clampedOrb = orbTolerance.coerceIn(1.0, 10.0)
        return natal.flatMap { natalPosition ->
            transit.mapNotNull { transitPosition ->
                val separation = angularSeparation(natalPosition.zodiacDegree, transitPosition.zodiacDegree)
                val match = AspectType.entries
                    .map { aspect -> aspect to abs(separation - aspect.angle) }
                    .filter { (_, orb) -> orb <= clampedOrb }
                    .minByOrNull { (_, orb) -> orb }

                match?.let { (type, orb) ->
                    Aspect(
                        natal = natalPosition,
                        transit = transitPosition,
                        type = type,
                        orb = orb,
                        strength = (1.0 - (orb / clampedOrb)).coerceIn(0.08, 1.0).toFloat()
                    )
                }
            }
        }.sortedWith(compareByDescending<Aspect> { it.strength }.thenBy { it.orb })
            .take(36)
    }
}

private fun Instant.toAstronomyTime(): Time {
    val utc = atZone(ZoneOffset.UTC)
    val seconds = utc.second + (utc.nano / 1_000_000_000.0)
    return Time(utc.year, utc.monthValue, utc.dayOfMonth, utc.hour, utc.minute, seconds)
}

private fun Double.normalizedDegrees(): Double =
    ((this % 360.0) + 360.0) % 360.0

private fun signedAngularDelta(start: Double, end: Double): Double {
    var delta = (end.normalizedDegrees() - start.normalizedDegrees() + 540.0) % 360.0 - 180.0
    if (delta == -180.0) delta = 180.0
    return delta
}

private fun angularSeparation(a: Double, b: Double): Double =
    abs(signedAngularDelta(a, b)).coerceAtMost(180.0)
