package com.esandi.healthmatecore.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.esandi.healthmatecore.data.DayTotals
import com.esandi.healthmatecore.records.MetricFilter
import com.esandi.healthmatecore.ui.theme.BrightBlue
import com.esandi.healthmatecore.ui.theme.ErrorRed
import com.esandi.healthmatecore.ui.theme.RingLimeDark
import com.esandi.healthmatecore.ui.theme.RingLimeLight
import com.esandi.healthmatecore.ui.theme.RingMintDark
import com.esandi.healthmatecore.ui.theme.RingMintLight
import com.esandi.healthmatecore.ui.theme.SoftViolet
import com.esandi.healthmatecore.ui.theme.SuccessGreen
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    val days = (0..6).map { weekStart.plusDays(it.toLong()) }
    val today = LocalDate.now()

    val values = days.map { day ->
        val totals = weekTotals.firstOrNull { it.date == day.toString() }

        when (metric) {
            MetricFilter.STEPS -> totals?.steps ?: 0
            MetricFilter.CALORIES -> totals?.calories ?: 0
            MetricFilter.WATER -> totals?.waterMl ?: 0
            MetricFilter.SLEEP -> totals?.sleepMinutes ?: 0
            MetricFilter.ALL -> 0
        }
    }

    val goal = when (metric) {
        MetricFilter.STEPS -> 8000
        MetricFilter.CALORIES -> 400
        MetricFilter.WATER -> 2000
        MetricFilter.SLEEP -> 480
        MetricFilter.ALL -> 0
    }

    val barColor = when (metric) {
        MetricFilter.STEPS ->
            if (isSystemInDarkTheme()) RingMintDark else RingMintLight

        MetricFilter.CALORIES -> ErrorRed
        MetricFilter.WATER -> BrightBlue
        MetricFilter.SLEEP -> SoftViolet
        MetricFilter.ALL -> MaterialTheme.colorScheme.primary
    }

    val goalTopColor = if (metric == MetricFilter.STEPS) {
        if (isSystemInDarkTheme()) RingLimeDark else RingLimeLight
    } else {
        barColor
    }

    val todayIndex = days.indexOf(today)
    val futureFrom = if (todayIndex >= 0) todayIndex + 1 else 7

    val daysSoFar = futureFrom
    val counted = values.take(daysSoFar)
    val total = counted.sum()
    val average = if (daysSoFar > 0) total / daysSoFar else 0
    val goalDays = counted.count { it >= goal }

    val unit = when (metric) {
        MetricFilter.STEPS -> "steps"
        MetricFilter.CALORIES -> "kcal"
        MetricFilter.WATER -> "ml"
        MetricFilter.SLEEP -> ""
        MetricFilter.ALL -> ""
    }

    fun summaryValue(v: Int, metric: MetricFilter): String =
        when (metric) {
            MetricFilter.SLEEP -> "${v / 60}h ${v % 60}m"
            else -> "%,d".format(v)
        }

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

            WeekBarChart(
                values = values,
                goal = goal,
                todayIndex = todayIndex,
                futureFrom = futureFrom,
                color = barColor,
                goalTopColor = goalTopColor,
                metric = metric
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryCard(
                label = "Total",
                value = summaryValue(total, metric),
                unit = unit,
                modifier = Modifier.weight(1f)
            )

            SummaryCard(
                label = "Daily avg",
                value = summaryValue(average, metric),
                unit = unit,
                modifier = Modifier.weight(1f)
            )

            SummaryCard(
                label = "Goal met",
                value = "$goalDays",
                unit = "of $daysSoFar days",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun shortValue(
    value: Int,
    metric: MetricFilter
): String {
    return when (metric) {
        MetricFilter.STEPS ->
            if (value >= 1000) "%.1fk".format(value / 1000f) else "$value"

        MetricFilter.SLEEP -> "${value / 60}h"

        else -> "$value"
    }
}

@Composable
private fun WeekBarChart(
    values: List<Int>,
    goal: Int,
    todayIndex: Int,
    futureFrom: Int,
    color: Color,
    goalTopColor: Color,
    metric: MetricFilter
) {
    val chartHeight = 140.dp
    val top = (
            maxOf(values.maxOrNull() ?: 0, goal) * 1.15f
            ).coerceAtLeast(1f)

    val labels = listOf("M", "T", "W", "T", "F", "S", "S")
    val lineColor = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(chartHeight + 40.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight + 24.dp)
        ) {
            val y = size.height -
                    (goal / top) * chartHeight.toPx()

            drawLine(
                color = lineColor.copy(alpha = 0.6f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(10f, 8f)
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.Bottom
        ) {
            values.forEachIndexed { index, value ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    if (value > 0 && index < futureFrom) {
                        Text(
                            text = shortValue(value, metric),
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1
                        )
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (index < futureFrom && value > 0) {
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(
                                    chartHeight * (value / top)
                                )
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 8.dp,
                                        topEnd = 8.dp,
                                        bottomStart = 4.dp,
                                        bottomEnd = 4.dp
                                    )
                                )
                                .background(
                                    when {
                                        value >= goal ->
                                            Brush.verticalGradient(
                                                colors = listOf(goalTopColor, color)
                                            )

                                        index == todayIndex -> SolidColor(color)

                                        else -> SolidColor(color.copy(alpha = 0.45f))
                                    }
                                )
                        )
                    } else {
                        Spacer(modifier = Modifier.height(2.dp))
                    }

                    Text(
                        text = labels[index],
                        style = MaterialTheme.typography.labelSmall,
                        color = if (index == todayIndex) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(10.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1
        )

        Text(
            text = unit,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}