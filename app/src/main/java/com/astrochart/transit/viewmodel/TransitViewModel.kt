package com.astrochart.transit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astrochart.transit.engine.AstronomyEngineCalculator
import com.astrochart.transit.engine.CelestialCalculator
import com.astrochart.transit.model.ChartState
import com.astrochart.transit.model.ObserverLocation
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

enum class ScrubSpan(val label: String, val days: Double) {
    Week("7D", 7.0),
    Month("30D", 30.0),
    Quarter("90D", 90.0),
    Year("1Y", 365.0)
}

data class TransitUiState(
    val chartState: ChartState = ChartState.Empty,
    val natalInput: String = "",
    val transitInput: String = "",
    val inputError: String? = null,
    val isCalculating: Boolean = true,
    val scrubValue: Float = 0.5f,
    val scrubSpan: ScrubSpan = ScrubSpan.Quarter,
    val orbTolerance: Float = 6f,
    val isPlaying: Boolean = false
)

class TransitViewModel(
    private val calculator: CelestialCalculator = AstronomyEngineCalculator()
) : ViewModel() {
    private val initialInstant = Instant.now()
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneOffset.UTC)
    private val parser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    private val natalInstant = MutableStateFlow(initialInstant)
    private val transitAnchor = MutableStateFlow(initialInstant)
    private val scrubOffsetDays = MutableStateFlow(0.0)
    private val scrubSpan = MutableStateFlow(ScrubSpan.Quarter)
    private val orbTolerance = MutableStateFlow(6.0)
    private var playbackJob: Job? = null

    private val _uiState = MutableStateFlow(
        TransitUiState(
            natalInput = formatInstant(natalInstant.value),
            transitInput = formatInstant(transitAnchor.value)
        )
    )
    val uiState: StateFlow<TransitUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                natalInstant,
                transitAnchor,
                scrubOffsetDays,
                scrubSpan,
                orbTolerance
            ) { natal, anchor, offset, span, orb ->
                CalculationParams(
                    natal = natal,
                    transit = anchor.plus(Duration.ofMillis((offset * 86_400_000.0).toLong())),
                    span = span,
                    offset = offset,
                    orbTolerance = orb,
                    observerLocation = ObserverLocation()
                )
            }.collectLatest { params ->
                _uiState.update { it.copy(isCalculating = true, inputError = null) }
                val chart = withContext(Dispatchers.Default) {
                    calculator.calculateChart(
                        natalInstant = params.natal,
                        transitInstant = params.transit,
                        observerLocation = params.observerLocation,
                        orbTolerance = params.orbTolerance
                    )
                }
                _uiState.update {
                    it.copy(
                        chartState = chart,
                        transitInput = formatInstant(params.transit),
                        isCalculating = false,
                        scrubValue = (((params.offset / params.span.days) + 1.0) / 2.0)
                            .coerceIn(0.0, 1.0)
                            .toFloat(),
                        scrubSpan = params.span,
                        orbTolerance = params.orbTolerance.toFloat()
                    )
                }
            }
        }
    }

    fun onNatalInputChanged(value: String) {
        _uiState.update { it.copy(natalInput = value, inputError = null) }
    }

    fun onTransitInputChanged(value: String) {
        _uiState.update { it.copy(transitInput = value, inputError = null) }
    }

    fun applyNatalInput() {
        parseInput(_uiState.value.natalInput)
            .onSuccess { parsed ->
                natalInstant.value = parsed
                _uiState.update { it.copy(natalInput = formatInstant(parsed), inputError = null) }
            }
            .onFailure { showParseError("Natal UTC") }
    }

    fun applyTransitInput() {
        parseInput(_uiState.value.transitInput)
            .onSuccess { parsed ->
                transitAnchor.value = parsed
                scrubOffsetDays.value = 0.0
                _uiState.update { it.copy(transitInput = formatInstant(parsed), inputError = null) }
            }
            .onFailure { showParseError("Transit UTC") }
    }

    fun setScrubFraction(value: Float) {
        val fraction = value.coerceIn(0f, 1f).toDouble()
        scrubOffsetDays.value = ((fraction * 2.0) - 1.0) * scrubSpan.value.days
    }

    fun setScrubSpan(span: ScrubSpan) {
        scrubSpan.value = span
        scrubOffsetDays.value = scrubOffsetDays.value.coerceIn(-span.days, span.days)
    }

    fun setOrbTolerance(value: Float) {
        orbTolerance.value = value.toDouble().coerceIn(1.0, 10.0)
    }

    fun resetTransitToNow() {
        transitAnchor.value = Instant.now()
        scrubOffsetDays.value = 0.0
    }

    fun nudgeTransit(days: Long) {
        transitAnchor.value = currentTransitInstant().plus(Duration.ofDays(days))
        scrubOffsetDays.value = 0.0
    }

    fun togglePlayback() {
        if (playbackJob?.isActive == true) {
            playbackJob?.cancel()
            playbackJob = null
            _uiState.update { it.copy(isPlaying = false) }
            return
        }

        _uiState.update { it.copy(isPlaying = true) }
        playbackJob = viewModelScope.launch {
            while (true) {
                delay(250)
                val next = scrubOffsetDays.value + 0.0625
                if (next > scrubSpan.value.days) {
                    transitAnchor.value = currentTransitInstant()
                    scrubOffsetDays.value = 0.0
                } else {
                    scrubOffsetDays.value = next
                }
            }
        }
    }

    override fun onCleared() {
        playbackJob?.cancel()
        super.onCleared()
    }

    private fun currentTransitInstant(): Instant =
        transitAnchor.value.plus(Duration.ofMillis((scrubOffsetDays.value * 86_400_000.0).toLong()))

    private fun parseInput(value: String): Result<Instant> =
        try {
            Result.success(LocalDateTime.parse(value.trim(), parser).toInstant(ZoneOffset.UTC))
        } catch (error: DateTimeParseException) {
            Result.failure(error)
        }

    private fun showParseError(label: String) {
        _uiState.update { it.copy(inputError = "$label must be yyyy-MM-dd HH:mm UTC") }
    }

    private fun formatInstant(instant: Instant): String = formatter.format(instant)

    private data class CalculationParams(
        val natal: Instant,
        val transit: Instant,
        val span: ScrubSpan,
        val offset: Double,
        val orbTolerance: Double,
        val observerLocation: ObserverLocation
    )
}
