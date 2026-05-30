package com.astrochart.transit.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.astrochart.transit.ui.theme.LineGrey
import com.astrochart.transit.ui.theme.SolarGold
import com.astrochart.transit.viewmodel.ScrubSpan
import com.astrochart.transit.viewmodel.TransitUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TimeScrubber(
    uiState: TransitUiState,
    onScrubChanged: (Float) -> Unit,
    onSpanSelected: (ScrubSpan) -> Unit,
    onOrbChanged: (Float) -> Unit,
    onResetToNow: () -> Unit,
    onNudge: (Long) -> Unit,
    onTogglePlayback: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, LineGrey)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Time Scrubber",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(onClick = { onNudge(-1) }, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Rounded.KeyboardArrowLeft, contentDescription = "Back one day")
                }
                IconButton(onClick = onTogglePlayback, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = if (uiState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (uiState.isPlaying) "Pause" else "Play"
                    )
                }
                IconButton(onClick = { onNudge(1) }, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Rounded.KeyboardArrowRight, contentDescription = "Forward one day")
                }
                IconButton(onClick = onResetToNow, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Rounded.RestartAlt, contentDescription = "Now")
                }
            }

            Slider(
                value = uiState.scrubValue,
                onValueChange = onScrubChanged,
                modifier = Modifier.fillMaxWidth()
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ScrubSpan.entries.forEach { span ->
                    FilterChip(
                        selected = uiState.scrubSpan == span,
                        onClick = { onSpanSelected(span) },
                        label = { Text(span.label) }
                    )
                }
                ElevatedButton(
                    onClick = onResetToNow,
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = SolarGold,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("NOW")
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Orb ${"%.1f".format(uiState.orbTolerance)}°",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Slider(
                    value = uiState.orbTolerance,
                    onValueChange = onOrbChanged,
                    valueRange = 1f..10f,
                    steps = 17
                )
            }
        }
    }
}

@Composable
fun EphemerisInputs(
    uiState: TransitUiState,
    onNatalChanged: (String) -> Unit,
    onTransitChanged: (String) -> Unit,
    onApplyNatal: () -> Unit,
    onApplyTransit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, LineGrey)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DateField(
                label = "Natal UTC",
                value = uiState.natalInput,
                onValueChange = onNatalChanged,
                onApply = onApplyNatal
            )
            DateField(
                label = "Transit UTC",
                value = uiState.transitInput,
                onValueChange = onTransitChanged,
                onApply = onApplyTransit
            )
            uiState.inputError?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun DateField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onApply: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        trailingIcon = {
            IconButton(onClick = onApply, modifier = Modifier.size(44.dp)) {
                Icon(Icons.Rounded.Check, contentDescription = "Apply $label")
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 58.dp)
    )
}
