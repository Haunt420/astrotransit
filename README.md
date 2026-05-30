# AstroTransit

AstroTransit is a greenfield Android/Jetpack Compose app for a dual-wheel natal/transit chart with a real-time scrubber.

## Ephemeris

No placeholder ephemeris is used. Planetary longitudes come from [Astronomy Engine](https://github.com/cosinekitty/astronomy), pinned as `io.github.cosinekitty:astronomy:2.1.19` through JitPack. The app calculates apparent geocentric ecliptic longitudes locally for the Sun, Moon, Mercury, Venus, Mars, Jupiter, Saturn, Uranus, Neptune, and Pluto.

The default natal and transit timestamps are initialized to the current UTC instant so the first screen has a real chart immediately. Changing natal or transit UTC fields recalculates from the same real ephemeris path.

## UI

- Jetpack Compose + Material 3.
- Pure Compose `Canvas` dual wheel.
- Inner ring: natal positions.
- Outer ring: transit positions.
- Collision-aware glyph layout when bodies are within 5 degrees.
- Dynamic aspect web for conjunction, square, trine, and opposition.
- Slider-driven transit time scrubber with 7-day, 30-day, 90-day, and 1-year windows.
- Orb tolerance control and active aspect ledger.

## Build

Open the project in Android Studio or run with a local Gradle install:

```bash
gradle :app:assembleDebug
```

This checkout does not include generated ephemeris files or network-backed astrology APIs. Runtime calculations are local.
