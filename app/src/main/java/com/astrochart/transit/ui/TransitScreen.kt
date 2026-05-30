package com.astrochart.transit.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.astrochart.transit.model.Aspect
import com.astrochart.transit.model.AspectType
import com.astrochart.transit.model.ChartState
import com.astrochart.transit.model.PlanetPosition
import com.astrochart.transit.ui.theme.EclipseBlack
import com.astrochart.transit.ui.theme.LineGrey
import com.astrochart.transit.ui.theme.MarsRose
import com.astrochart.transit.ui.theme.SolarGold
import com.astrochart.transit.ui.theme.TransitTeal
import com.astrochart.transit.ui.theme.Verdant
import com.astrochart.transit.viewmodel.TransitUiState
import com.astrochart.transit.viewmodel.TransitViewModel

@Composable
fun TransitScreen(
    viewModel: TransitViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TransitScreenContent(
        uiState = uiState,
        onScrubChanged = viewModel::setScrubFraction,
        onSpanSelected = viewModel::setScrubSpan,
        onOrbChanged = viewModel::setOrbTolerance,
        onResetToNow = viewModel::resetTransitToNow,
        onNudge = viewModel::nudgeTransit,
        onTogglePlayback = viewModel::togglePlayback,
        onNatalChanged = viewModel::onNatalInputChanged,
        onTransitChanged = viewModel::onTransitInputChanged,
        onApplyNatal = viewModel::applyNatalInput,
        onApplyTransit = viewModel::applyTransitInput
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TransitScreenContent(
    uiState: TransitUiState,
    onScrubChanged: (Float) -> Unit,
    onSpanSelected: (com.astrochart.transit.viewmodel.ScrubSpan) -> Unit,
    onOrbChanged: (Float) -> Unit,
    onResetToNow: () -> Unit,
    onNudge: (Long) -> Unit,
    onTogglePlayback: () -> Unit,
    onNatalChanged: (String) -> Unit,
    onTransitChanged: (String) -> Unit,
    onApplyNatal: () -> Unit,
    onApplyTransit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EclipseBlack)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val wide = maxWidth >= 880.dp
            if (wide) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ChartColumn(uiState, Modifier.weight(1.35f).fillMaxHeight())
                    ControlColumn(
                        uiState = uiState,
                        onScrubChanged = onScrubChanged,
                        onSpanSelected = onSpanSelected,
                        onOrbChanged = onOrbChanged,
                        onResetToNow = onResetToNow,
                        onNudge = onNudge,
                        onTogglePlayback = onTogglePlayback,
                        onNatalChanged = onNatalChanged,
                        onTransitChanged = onTransitChanged,
                        onApplyNatal = onApplyNatal,
                        onApplyTransit = onApplyTransit,
                        modifier = Modifier.width(420.dp).fillMaxHeight()
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Header(uiState)
                    ChartSurface(uiState.chartState)
                    TimeScrubber(
                        uiState = uiState,
                        onScrubChanged = onScrubChanged,
                        onSpanSelected = onSpanSelected,
                        onOrbChanged = onOrbChanged,
                        onResetToNow = onResetToNow,
                        onNudge = onNudge,
                        onTogglePlayback = onTogglePlayback
                    )
                    EphemerisInputs(
                        uiState = uiState,
                        onNatalChanged = onNatalChanged,
                        onTransitChanged = onTransitChanged,
                        onApplyNatal = onApplyNatal,
                        onApplyTransit = onApplyTransit
                    )
                    PositionFlow(uiState.chartState.transitPositions)
                    AspectLedger(uiState.chartState.aspects)
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun ChartColumn(uiState: TransitUiState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Header(uiState)
        ChartSurface(
            chartState = uiState.chartState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

@Composable
private fun ControlColumn(
    uiState: TransitUiState,
    onScrubChanged: (Float) -> Unit,
    onSpanSelected: (com.astrochart.transit.viewmodel.ScrubSpan) -> Unit,
    onOrbChanged: (Float) -> Unit,
    onResetToNow: () -> Unit,
    onNudge: (Long) -> Unit,
    onTogglePlayback: () -> Unit,
    onNatalChanged: (String) -> Unit,
    onTransitChanged: (String) -> Unit,
    onApplyNatal: () -> Unit,
    onApplyTransit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TimeScrubber(
            uiState = uiState,
            onScrubChanged = onScrubChanged,
            onSpanSelected = onSpanSelected,
            onOrbChanged = onOrbChanged,
            onResetToNow = onResetToNow,
            onNudge = onNudge,
            onTogglePlayback = onTogglePlayback
        )
        EphemerisInputs(
            uiState = uiState,
            onNatalChanged = onNatalChanged,
            onTransitChanged = onTransitChanged,
            onApplyNatal = onApplyNatal,
            onApplyTransit = onApplyTransit
        )
        PositionFlow(uiState.chartState.transitPositions)
        AspectLedger(uiState.chartState.aspects)
    }
}

@Composable
private fun Header(uiState: TransitUiState) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "AstroTransit",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = uiState.chartState.source,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        AssistChip(
            onClick = {},
            label = { Text("REAL EPHEMERIS") },
            border = BorderStroke(1.dp, TransitTeal)
        )
        if (uiState.isCalculating) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp,
                color = SolarGold
            )
        }
    }
}

@Composable
private fun ChartSurface(
    chartState: ChartState,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = MaterialTheme.shapes.medium,
        color = EclipseBlack,
        border = BorderStroke(1.dp, LineGrey)
    ) {
        TransitChartCanvas(
            chartState = chartState,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PositionFlow(positions: List<PlanetPosition>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, LineGrey)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Transit Positions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                positions.forEach { position ->
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                text = "${position.celestialBody.glyph} ${position.formattedDegree}${if (position.isRetrograde) " R" else ""}",
                                maxLines = 1
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AspectLedger(aspects: List<Aspect>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, LineGrey)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Active Aspects",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (aspects.isEmpty()) {
                Text(
                    text = "None inside current orb",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                aspects.take(10).forEach { aspect ->
                    AspectRow(aspect)
                }
            }
        }
    }
}

@Composable
private fun AspectRow(aspect: Aspect) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier.width(4.dp).height(36.dp),
            color = aspectColor(aspect.type),
            shape = MaterialTheme.shapes.extraSmall,
            content = {}
        )
        Text(
            text = "${aspect.transit.celestialBody.glyph} ${aspect.type.glyph} ${aspect.natal.celestialBody.glyph}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = aspectColor(aspect.type),
            modifier = Modifier.width(74.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${aspect.transit.celestialBody.displayName} ${aspect.type.title} ${aspect.natal.celestialBody.displayName}",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "orb ${"%.2f".format(aspect.orb)}°",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun aspectColor(type: AspectType): Color =
    when (type) {
        AspectType.Conjunction -> SolarGold
        AspectType.Square -> MarsRose
        AspectType.Trine -> Verdant
        AspectType.Opposition -> TransitTeal
    }
