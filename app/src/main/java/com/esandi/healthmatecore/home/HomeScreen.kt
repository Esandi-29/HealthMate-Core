package com.esandi.healthmatecore.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esandi.healthmatecore.R
import com.esandi.healthmatecore.data.DailyTotals
import com.esandi.healthmatecore.data.Goals
import com.esandi.healthmatecore.data.HealthRecord
import com.esandi.healthmatecore.ui.theme.BrightBlue
import com.esandi.healthmatecore.ui.theme.CaloriesTextDark
import com.esandi.healthmatecore.ui.theme.CaloriesTextLight
import com.esandi.healthmatecore.ui.theme.DeepBlue
import com.esandi.healthmatecore.ui.theme.DeepPurple
import com.esandi.healthmatecore.ui.theme.ErrorRed
import com.esandi.healthmatecore.ui.theme.RingLimeDark
import com.esandi.healthmatecore.ui.theme.RingLimeLight
import com.esandi.healthmatecore.ui.theme.RingMintDark
import com.esandi.healthmatecore.ui.theme.RingMintLight
import com.esandi.healthmatecore.ui.theme.Roboto
import com.esandi.healthmatecore.ui.theme.SleepTextDark
import com.esandi.healthmatecore.ui.theme.SleepTextLight
import com.esandi.healthmatecore.ui.theme.SoftViolet
import com.esandi.healthmatecore.ui.theme.SunsetOrange
import com.esandi.healthmatecore.ui.theme.WaterTextDark
import com.esandi.healthmatecore.ui.theme.WaterTextLight
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@Composable
fun RecordsScreen(
    records: List<HealthRecord>,
    totals: DailyTotals,
    goals: Goals,
    onDatePicked: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        WelcomeHeader()

        WeekStrip(
            records = records,
            onDayClick = onDatePicked
        )

        TodaySummary(totals = totals, goals = goals)

        LatestRecords(records = records)


    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeHeaderPreview() {
    WelcomeHeader()
}

@Composable
fun WelcomeHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text(
            text = "Welcome back, Esandi",
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = Roboto,
                fontWeight = FontWeight.Normal
            )
        )
    }
}

@Composable
fun WeekStrip(
    records: List<HealthRecord>,
    onDayClick: (String) -> Unit
) {
    val today = LocalDate.now()
    val monday = today.with(
        TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
    )

    val weekDays = (0..6).map { monday.plusDays(it.toLong()) }
    val datesWithRecords = records.map { it.date }.toSet()
    val dotColor = if (isSystemInDarkTheme()) RingMintDark else RingMintLight

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        weekDays.forEach { date ->
            val isToday = date == today
            val hasRecords = date.toString() in datesWithRecords

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        onDayClick(date.toString())
                    }
                    .padding(vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = date.dayOfWeek.name.take(1),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (isToday) {
                                MaterialTheme.colorScheme.onBackground
                            } else {
                                Color.Transparent
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isToday) {
                            MaterialTheme.colorScheme.background
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }

                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(
                            if (hasRecords) {
                                dotColor
                            } else {
                                Color.Transparent
                            }
                        )
                )
            }
        }
    }
}

@Composable
fun TodaySummary(totals: DailyTotals, goals: Goals) {
    val sleepHours = totals.sleepMinutes / 60
    val sleepRemainder = totals.sleepMinutes % 60
    val dark = isSystemInDarkTheme()

    Column(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Today's summary",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            StepsRing(
                steps = totals.steps,
                goal = goals.steps
            )
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricTile(
                value = "%,d".format(totals.calories),
                label = "Calories",
                icon = R.drawable.ic_calories,
                color = if (dark) CaloriesTextDark else CaloriesTextLight,
                gradientStart = ErrorRed,
                gradientEnd = SunsetOrange,
                modifier = Modifier.weight(1f)
            )

            MetricTile(
                value = "${totals.waterMl} ml",
                label = "Water",
                icon = R.drawable.ic_water,
                color = if (dark) WaterTextDark else WaterTextLight,
                gradientStart = BrightBlue,
                gradientEnd = DeepBlue,
                modifier = Modifier.weight(1f)
            )

            MetricTile(
                value = "${sleepHours}h ${sleepRemainder}m",
                label = "Sleep",
                icon = R.drawable.ic_sleep,
                color = if (dark) SleepTextDark else SleepTextLight,
                gradientStart = SoftViolet,
                gradientEnd = DeepPurple,
                modifier = Modifier.weight(1f)
            )
        }
        WaterGoalBar(
            waterMl = totals.waterMl,
            goal = goals.waterMl
        )

    }
}

@Composable
fun StepsRing(
    steps: Int,
    goal: Int
) {
    val progress = if (goal > 0) {
        (steps.toFloat() / goal).coerceIn(0f, 1f)
    } else {
        0f
    }

    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val ringStart =
        if (isSystemInDarkTheme()) RingMintDark else RingMintLight

    val ringEnd =
        if (isSystemInDarkTheme()) RingLimeDark else RingLimeLight

    Box(
        modifier = Modifier.size(170.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val stroke = Stroke(
                width = 14.dp.toPx(),
                cap = StrokeCap.Round
            )

            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke
            )

            if (progress > 0f) {
                rotate(degrees = -90f) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            colorStops = arrayOf(
                                0f to ringStart,
                                progress to ringEnd,
                                1f to ringStart
                            ),
                            center = center
                        ),
                        startAngle = 0f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = stroke
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "%,d".format(steps),
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = "of %,d steps".format(goal),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MetricTile(
    value: String,
    label: String,
    icon: Int,
    color: Color,
    gradientStart: Color,
    gradientEnd: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        gradientStart.copy(alpha = if (isSystemInDarkTheme()) 0.20f else 0.14f),
                        gradientEnd.copy(alpha = if (isSystemInDarkTheme()) 0.20f else 0.14f)
                    )
                )
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = color,
            maxLines = 1
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun WaterGoalBar(
    waterMl: Int,
    goal: Int
) {
    val progress = if (goal > 0) {
        (waterMl.toFloat() / goal).coerceIn(0f, 1f)
    } else {
        0f
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Water: ${waterMl.coerceAtLeast(0)} of %,d ml".format(goal),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            drawStopIndicator = {}
        )
    }
}

@Composable
fun LatestRecords(records: List<HealthRecord>) {
    val lastSteps = records.firstOrNull { it.steps > 0 }
    val lastCalories = records.firstOrNull { it.calories > 0 }
    val lastWater = records.firstOrNull { it.waterMl > 0 }
    val lastSleep = records.firstOrNull { it.sleepMinutes > 0 }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Latest",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        lastSteps?.let { record ->
            Text(
                text = "Last steps: %,d on ${record.date}".format(record.steps),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        lastCalories?.let { record ->
            Text(
                text = "Last calories: %,d on ${record.date}".format(record.calories),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        lastWater?.let { record ->
            Text(
                text = "Last water: ${record.waterMl} ml on ${record.date}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        lastSleep?.let { record ->
            val hours = record.sleepMinutes / 60
            val minutes = record.sleepMinutes % 60

            Text(
                text = "Last sleep: ${hours}h ${minutes}m on ${record.date}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}