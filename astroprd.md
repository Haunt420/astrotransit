# AstroFuture Android Parity PRD

## Objective

Build a perfect Android port of the iOS astrology app, AstroFuture, matching the observable function, information architecture, interaction patterns, and polish of its iOS counterpart as closely as is possible, while avoiding direct copying of brand assets and/or copyrighted text.

The product should feel like a native Android counterpart to the iOS AstroFuture applicati9n:

- chart-first
- reading-heavy
- tappable and explorable
- personalized by birth data and profiles
- strong on transits, compatibility, and special events
- polished enough to work as a daily habit app

## Reference Sources

Public Astro Future sources used as the observable benchmark:

- [App Store listing](https://apps.apple.com/us/app/astro-future-daily-horoscope/id1119123378)
- [Official site](https://www.astro-future.com/)
- [Elements, Qualities & Dualities](https://www.astro-future.com/elements-qualities-dualities)
- [Love Compatibility](https://www.astro-future.com/love-compatibility)
- [Terms of Use](https://www.astro-future.com/terms-of-use)
- [Subscription Terms](https://www.astro-future.com/subscription-terms)
- [Privacy Policy](https://www.astro-future.com/privacy-policy)

## Product Constraints

### Must

- Be an Android app built with Kotlin and Jetpack Compose.
- Preserve the same core user value: chart-based astrology readings, transit exploration, compatibility analysis, and profile management.
- Support multiple saved people: self, family, friends, and crushes.
- Include PDF/report generation and shareable visual snapshots.
- Support astrology reference content for elements, qualities, dualities, house systems, and special events.
- Handle unknown birth time explicitly.
- Strive to reproduce exact UI composition nearly pixel-for-pixel.

## Application Principles

1. **Interactive Transit Chart is Keystone**: the wheel or chart view is the anchor, not an afterthought.
2. **Reveal on demand**: aspect and reading detail should appear through progressive disclosure.
3. **Multiple people are core data**: the app should handle several saved profiles naturally.
4. **Entertainment tone**: interpretations should be positive, clear, and non-clinical.
5. **Motion with purpose**: animations should communicate planetary movement, not just decorate.
6. **Android-native behavior**: adaptive layouts, back handling, and shares should feel standard on Android.

## Information Architecture

Primary surfaces:

- Home
- Chart
- Transits
- Compatibility
- People / Profiles
- Reports
- Learn / Reference
- Settings

Secondary surfaces:

- Paywall
- Export / Share
- Notifications preferences
- Subscription management
- Privacy / legal

## Functional Parity Matrix

| Astro Future observable feature | Android equivalent |
|---|---|
| Birth charts with tappable aspects | Natal chart wheel with tappable aspects and bottom sheet detail |
| Daily horoscope in app | Home daily reading, and notifications |
| Transit charts with future scrolling | Interactive transit timeline and date scrubber |
| Animated solar system view | Animated orbit/solar-system visualization |
| Synastry charts | Two-person compatibility chart and relationship analysis |
| Many saved family/friend/crush profiles | Profile vault with unlimited or tiered saves |
| PDF reports | In-app PDF export and share |
| Horoscope snapshot sharing | Rendered share card export |
| Elements, qualities, dualities | Reference library and chart breakdown |
| Compatibility tiers: elements, harmony, passion | Dedicated compatibility result sections |
| Special events: eclipses, solstices, equinoxes, retrogrades, planetary birthdays | Event timeline and event detail cards |
| House system selection | Chart settings and profile-specific house system preference |

## Screen-by-Screen PRD

### 1. Splash / App Launch

**Purpose**

- Establish brand immediately.
- Load local profiles, entitlement state, and cached chart data.

**Layout**

- App mark centered.
- Subtle animated background or moving celestial gradient.
- No interactive controls.

**States**

- Cold launch
- First launch
- Loading cached data
- Loading remote sync or interpretation pack
- Error fallback

**Acceptance Criteria**

- The app reaches a usable screen within a short, non-blocking launch path.
- Cached profiles appear even if the network is unavailable.
- Launch errors degrade gracefully to an offline-capable home or retry screen.

### 2. Onboarding / Birth Data Entry

**Purpose**

- Collect the minimum data needed to generate a natal chart and daily readings.

**Fields**

- Name or nickname
- Birth date
- Birth time
- Birth location
- Timezone confirmation
- Unknown time toggle
- Profile type, if useful: self, partner, family, friend, crush

**Behavior**

- Unknown time must explicitly switch the chart into a time-unknown mode.
- Location search should support city and postal-code style search.
- The flow should explain why time accuracy matters without being verbose.

**States**

- Empty form
- Partial form
- Validation errors
- Unknown time mode
- Permission prompt for location autofill, if used

**Acceptance Criteria**

- User can complete onboarding without creating an account.
- User can save a profile with unknown birth time.
- Validation prevents impossible dates and missing required fields.
- Onboarding ends with a visible natal chart preview or a clear “continue to home” step.

### 3. Home Dashboard

**Purpose**

- Be the default daily entry point.
- Surface today’s reading, the active profile, and next important event.

**Layout**

- Top bar with active profile switcher.
- Daily horoscope summary card.
- Chart snapshot card.
- Upcoming event card.
- Quick actions for chart, transits, compatibility, and report export.

**Behavior**

- Switching profiles should immediately refresh all cards.
- The daily reading should feel personalized to the selected profile.

**States**

- Fresh daily reading
- Cached reading
- Loading
- No profiles yet

**Acceptance Criteria**

- The home screen shows a daily reading for the selected profile.
- Tapping the chart snapshot opens the natal chart.
- Tapping the event card opens the relevant transit or special-event detail.
- The home screen remains readable at phone, foldable, and tablet widths.

### 4. Natal Chart

**Purpose**

- Provide the core chart-centric experience.

**Layout**

- Large chart wheel or radial chart visualization.
- Chart legend / planet list.
- Reading stack or scrollable interpretation list.
- Toggle between visual and list-first emphasis if needed.

**Interactions**

- Tap planet, aspect, or house segment to reveal details.
- Highlight corresponding list items when chart items are tapped.
- Zoom or pinch only if it improves readability, not as a gimmick.

**States**

- Chart loaded
- Missing exact time
- Partial chart with reduced precision
- Loading interpretations
- Chart cache unavailable

**Acceptance Criteria**

- Users can tap chart elements and get contextual interpretation.
- The chart remains stable and legible on small and large screens.
- The app visually distinguishes planets, signs, houses, and aspects.
- Unknown birth time reduces precision in a way the user can understand.

### 5. Aspect Detail Bottom Sheet

**Purpose**

- Reveal the hidden meaning of a specific aspect or placement.

**Layout**

- Aspect title
- Plain-language summary
- Deeper interpretation text
- Supporting chart references

**Behavior**

- Open from a tap on the chart or reading list.
- Support swipe-down dismissal and back button dismissal.

**Acceptance Criteria**

- Every tappable aspect opens a detail view with chart-linked context.
- Detail text clearly explains what the aspect means in plain language.
- The detail sheet can be opened repeatedly without breaking chart selection state.

### 6. Daily Horoscope / Reading Detail

**Purpose**

- Provide the daily personalized reading that keeps the app habit-forming.

**Layout**

- Headline
- Short summary
- Long-form explanation
- Relevant chart references
- Optional action to save, share, or open compatibility

**Behavior**

- Should read as positive guidance, not deterministic fate.
- Should be linked to the current profile and current date.

**Acceptance Criteria**

- Each profile gets a clearly separate daily reading.
- The reading updates when the selected profile changes.
- The reading can be shared as a snapshot or card.

### 7. Transit Chart

**Purpose**

- Let the user explore the future by date and planetary position.

**Layout**

- Future timeline scrubber
- Transit date indicator
- Chart wheel or orbit visualization
- List of notable transiting bodies and aspects

**Behavior**

- User can scroll or scrub through time, include s rubbers for minute, hour, day/date, month, and year period time scrubbers. 
- Scrubbing should animate planetary movement smoothly.
- The app should mark notable future events on the timeline.

**States**

- Future date loading
- Event markers on/off

**Acceptance Criteria**

- The user can move through future dates and see the chart update.
- Events and readings change in response to the selected transit date.
- The time range respects entitlement limits.

### 8. Solar System / Orbit View

**Purpose**

- Provide the “planets in motion” surface that makes the app feel alive.

**Layout**

- Centered sun or chart anchor.
- Animated planetary orbits.
- Current date/time indicator.
- Tap targets for planet detail.

**Behavior**

- The motion should feel scientifically styled, even though the content is interpretive.
- Animation speed should support both casual browsing and precise inspection.

**Acceptance Criteria**

- Users can visually track planetary movement through the zodiac.
- Planet tap targets work reliably.
- The view remains performant on midrange Android devices.

### 9. Compatibility / Synastry

**Purpose**

- Compare two profiles and explain relationship fit.

**Layout**

- Two-person selector
- Compatibility chart or circular visualization
- Three major sections:
  - matching elements
  - harmony
  - passion
- Summary score or qualitative label, if used

**Behavior**

- The user can compare any two saved profiles.
- The result should be readable even for casual users who do not know astrology terminology.

**Acceptance Criteria**

- Any two profiles can be selected for comparison.
- The result includes distinct sections for matching elements, harmony, and passion.
- The user can save or share the result.

### 10. Compatibility Drill-Down

**Purpose**

- Explain why a compatibility result looks the way it does.

**Layout**

- Sun/Moon element match explanation
- Sun/Moon aspect harmony explanation
- Venus/Mars passion explanation
- Related chart references

**Acceptance Criteria**

- Each compatibility subsection is explainable on its own.
- The app ties high-level results back to chart components.


### 11. Profiles / People Vault

**Purpose**

- Store and manage multiple people cleanly.

**Layout**

- Profile list/grid
- Profile creation CTA
- Search or filter, if the list becomes large
- Profile type badges

**Behavior**

- Profiles should be editable, duplicable, archived, and deleted.
- The app should support self, partner, family, friend, and crush as labels or categories.

**Acceptance Criteria**

- Users can create multiple profiles without logging in.
- Users can edit birth data after creation.
- Users can reuse profiles in compatibility and transit screens.

### 12. Reports / PDF Export

**Purpose**

- Offer a more permanent, shareable astrology artifact.

**Layout**

- Report template chooser
- Preview
- Export and share actions
- Include current profile, chart, and relevant readings

**Behavior**

- Generate PDF locally or through a safe rendering pipeline.
- Preserve typography and chart fidelity in the exported file.

**Acceptance Criteria**

- Users can export a PDF report for the active profile.
- The PDF includes chart data, reading sections, and relevant metadata.
- Exported output is shareable through Android share sheet.


### 14. Reference Library

**Purpose**

- Explain the astrology system used by the app.

**Sections**

- Elements
- Qualities
- Dualities
- House systems
- Special events
- Planet meanings

**Behavior**

- Use concise, educational cards with drill-down detail.
- Make terminology consistent across chart, compatibility, and transits.

**Acceptance Criteria**

- Users can look up the meaning of elements, qualities, and dualities.
- The library is internally linked from chart interpretations.
- The content is searchable or browsable in a predictable way.

### 15. Special Events

**Purpose**

- Surface eclipses, solstices, equinoxes, retrogrades, and planetary birthdays.

**Layout**

- Event timeline
- Event detail page
- Relevance to active profile

**Behavior**

- Events should appear in the transit timeline and as dedicated cards.
- The app should distinguish astronomical events from astrological events.

**Acceptance Criteria**

- The app can identify and present major special events.
- Each event includes a readable explanation and date context.


### 16. House Systems Settings

**Purpose**

- Let the user choose how charts are interpreted.

**Layout**

- List of supported systems
- Short explanation for each
- Active selection indicator

**Acceptance Criteria**

- Users can change house systems from settings or within relevant chart views.
- The choice persists per profile or globally, according to the designed rule.
- Chart and interpretation refresh after a house system change.


**Acceptance Criteria**

- Notifications are opt-in.
- Notification copy is calm and clear.
- Tapping a notification opens the specific reading or event.

### 20. Settings

**Purpose**

- Centralize app preferences and legal controls.

**Sections**

- Theme
- Notification preferences
- Default profile
- House system
- Privacy controls
- Export / backup
- About / legal

**Acceptance Criteria**

- Settings are reachable from every primary screen.
- Users can reset the local app state without uninstalling the app.

## State and Edge Case Requirements

### Unknown Birth Time

- Must be first-class, not a hidden workaround.
- The UI should show when chart precision is reduced.
- Compatibility and transit detail should degrade gracefully.

### Missing Location

- Allow manual entry.
- Explain why location improves chart accuracy.

### Offline Mode

- Previously generated charts and profile data should remain available.
- The app should show cached readings with a clear freshness indicator.

### No Premium, All Free. 

### Empty Account

- A new user should not hit dead ends.
- Every empty screen should point directly to profile creation or a sample chart.

### Large Profile List

- Search, sort, or archive once the user has many saved people.

## Non-Functional Requirements

- **Performance**: chart rendering and timeline scrubbing should feel smooth on midrange devices.
- **Accessibility**: every chart element needs a semantic label and a keyboard/screen-reader path where practical.
- **Reliability**: chart generation should be deterministic for the same inputs and settings. 
- **Observability**: analytics and crash reporting should not log raw birth details.

## Recommended Android Stack

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Hilt
- Room
- DataStore
- WorkManager
- 
## Delivery Phasing

### Phase 1

- App shell
- Onboarding
- Local profile storage
- Home
- Natal chart

### Phase 2

- Transit timeline
- Solar system visualization
- Compatibility
- Reference library

### Phase 3

- Reports
- Notifications

### Phase 4

- House systems
- Special events
- Accessibility hardening
- Performance tuning

## Acceptance Summary

The app is acceptable when:

- a user can create several profiles and get personalized daily readings
- chart elements are tappable and explained in detail
- transits can be scrubbed through time
- compatibility works for any two saved profiles
- the app feels like an Android-native crossover to iOS's AstroFuture 

## Decisions

- Whether compatibility gets both a numerical score, and a qualitative score. 
- Profile data stays local-only
- Wear OS is not included in any way
- The free tier is the only tier. All content, the full featured app, is free. 
- Astrology interpretation content is fetched dynamically

## Appendix: Public Feature Notes

The public Astro Future listing and site describe:

- birth charts with tappable aspects
- transit charts with future scrubbing
- an animated solar-system style view
- synastry and love compatibility
- multiple saved people
- elements, qualities, and dualities
- time scrubbing range for transit wheel from 1900 to 2050
- detailed transit interpretations
- special events such as eclipses, solstices, equinoxes, retrogrades, and planetary birthdays
- selectable house systems

