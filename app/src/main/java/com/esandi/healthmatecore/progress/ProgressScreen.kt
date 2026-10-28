package com.esandi.healthmatecore.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.esandi.healthmatecore.data.DayTotals
import com.esandi.healthmatecore.records.MetricFilter
import com.esandi.healthmatecore.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.ui.Alignment

@Composable
fun ProgressScreen(
    weekTotals: List<DayTotals>,
    weekStart: LocalDate,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit
) {
    var metric by rememberSaveable {
        mutableStateOf(MetricFilter.STEPS)
    }
    var period by rememberSaveable {
        mutableStateOf("Week")
    }

    val faintOutline = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.16f)

    val solidBlueColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primary,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
    )

    val dateFormatter = DateTimeFormatter.ofPattern("d MMM")
    val weekEnd = weekStart.plusDays(6)
    val weekLabel = "${weekStart.format(dateFormatter)} to ${weekEnd.format(dateFormatter)}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Title
        Text(
            text = "Progress",
            style = MaterialTheme.typography.headlineLarge
        )

        // 2. Day / Week / Month chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Day", "Week", "Month").forEach { option ->
                FilterChip(
                    selected = period == option,
                    onClick = { period = option },
                    label = {
                        Text(
                            text = option,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = solidBlueColors,
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = period == option,
                        borderColor = faintOutline,
                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                        borderWidth = 0.5.dp
                    )
                )
            }
        }

        // 3. Metric pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricFilter.entries
                .filter { it != MetricFilter.ALL }
                .forEach { option ->
                    val metricColor = when (option) {
                        MetricFilter.STEPS -> SuccessGreen
                        MetricFilter.CALORIES -> ErrorRed
                        MetricFilter.WATER -> BrightBlue
                        MetricFilter.SLEEP -> SoftViolet
                        MetricFilter.ALL -> MaterialTheme.colorScheme.primary
                    }

                    FilterChip(
                        selected = metric == option,
                        onClick = { metric = option },
                        label = { Text(option.label) },
                        shape = CircleShape,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = metricColor.copy(alpha = 0.22f),
                            selectedLabelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = metric == option,
                            borderColor = faintOutline,
                            selectedBorderColor = metricColor.copy(alpha = 0.4f),
                            borderWidth = 0.5.dp,
                            selectedBorderWidth = 1.dp
                        )
                    )
                }
        }

        // 4. Week card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreviousWeek) {
                    Text("‹", style = MaterialTheme.typography.headlineMedium)
                }

                Text(
                    text = weekLabel,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )

                IconButton(onClick = onNextWeek) {
                    Text("›", style = MaterialTheme.typography.headlineMedium)
                }
            }

            // Part 2: the chart will go here.
        }
    }
}