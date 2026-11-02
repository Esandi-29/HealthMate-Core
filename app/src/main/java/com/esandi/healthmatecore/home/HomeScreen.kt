package com.esandi.healthmatecore.home

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esandi.healthmatecore.R
import com.esandi.healthmatecore.data.DailyTotals
import com.esandi.healthmatecore.data.Goals
import com.esandi.healthmatecore.data.HealthRecord
import com.esandi.healthmatecore.records.dayLabel
import com.esandi.healthmatecore.records.recordSummary
import com.esandi.healthmatecore.ui.theme.BrightBlue
import com.esandi.healthmatecore.ui.theme.CaloriesTextDark
import com.esandi.healthmatecore.ui.theme.CaloriesTextLight
import com.esandi.healthmatecore.ui.theme.DeepBlue
import com.esandi.healthmatecore.ui.theme.DeepPurple
import com.esandi.healthmatecore.ui.theme.ErrorRed
import com.esandi.healthmatecore.ui.theme.HealthMateCoreTheme
import com.esandi.healthmatecore.ui.theme.ProfileAmberStart
import com.esandi.healthmatecore.ui.theme.ProfileOrangeEnd
import com.esandi.healthmatecore.ui.theme.RingLimeDark
import com.esandi.healthmatecore.ui.theme.RingLimeLight
import com.esandi.healthmatecore.ui.theme.RingMintDark
import com.esandi.healthmatecore.ui.theme.RingMintLight
import com.esandi.healthmatecore.ui.theme.Roboto
import com.esandi.healthmatecore.ui.theme.SleepTextDark
import com.esandi.healthmatecore.ui.theme.SleepTextLight
import com.esandi.healthmatecore.ui.theme.SoftBlack
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
    streak: Int,
    totals: DailyTotals,
    goals: Goals,
    onDatePicked: (String) -> Unit,
    onRecordClick: (Long) -> Unit,
    onSeeAllClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        WelcomeHeader()

        Spacer(modifier = Modifier.height(12.dp))

        StreakBar(
            streak = streak,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        WeekStrip(
            records = records,
            onDayClick = onDatePicked
        )

        TodaySummary(totals = totals, goals = goals)

        RecentRecordsCard(
            records = records,
            onRecordClick = onRecordClick,
            onSeeAllClick = onSeeAllClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )


    }
}

@Composable
fun StreakBar(
    streak: Int,
    modifier: Modifier = Modifier
) {
    val progress = streak.coerceAtLeast(0).coerceAtMost(7)
    val progressFraction = progress / 7f
    val pillShape = RoundedCornerShape(50)

    // Infinite animation for the streak flame
    val transition = rememberInfiniteTransition(label = "flamePulse")

    // Gentle size pulse
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flameScale"
    )

    // Subtle brightness flicker
    val flicker by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flameAlpha"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(30.dp)
            .clip(pillShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                pillShape
            )
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_flame),
            contentDescription = null,
            modifier = Modifier
                .size(16.dp)
                .scale(if (streak > 0) pulse else 1f)
                .alpha(if (streak > 0) flicker else 1f),
            tint = ProfileOrangeEnd
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = if (streak <= 0) {
                "Start your streak today"
            } else {
                "$streak-day streak"
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.outlineVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressFraction)
                    .fillMaxHeight()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                ProfileAmberStart,
                                ProfileOrangeEnd
                            )
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "$progress/7",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
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

    val sleepProgress = if (goals.sleepMinutes > 0) {
        (totals.sleepMinutes.toFloat() / goals.sleepMinutes.toFloat())
            .coerceIn(0f, 1f)
    } else {
        0f
    }

    var sleepStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        sleepStarted = true
    }

    val animatedSleepProgress by animateFloatAsState(
        targetValue = if (sleepStarted) sleepProgress else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "sleepProgress"
    )

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
                .height(IntrinsicSize.Min)
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
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )

            MetricTile(
                value = "${totals.waterMl} ml",
                label = "Water",
                icon = R.drawable.ic_water,
                color = if (dark) WaterTextDark else WaterTextLight,
                gradientStart = BrightBlue,
                gradientEnd = DeepBlue,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )

            MetricTile(
                value = "${sleepHours}h ${sleepRemainder}m",
                label = "Sleep",
                icon = R.drawable.ic_sleep,
                color = if (dark) SleepTextDark else SleepTextLight,
                gradientStart = SoftViolet,
                gradientEnd = DeepPurple,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                topContent = {
                    SleepDial(
                        fill = animatedSleepProgress
                    )
                }
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

    var started by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        started = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (started) progress else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "stepsProgress"
    )

    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val ringStart =
        if (isSystemInDarkTheme()) RingMintDark else RingMintLight

    val ringEnd =
        if (isSystemInDarkTheme()) RingLimeDark else RingLimeLight

    val footprintColor = if (isSystemInDarkTheme()) {
        SoftBlack
    } else {
        Color.White
    }

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

            if (animatedProgress > 0f) {

                // Green arc — draw first
                rotate(degrees = -90f) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            colorStops = arrayOf(
                                0f to ringStart,
                                animatedProgress to ringEnd,
                                1f to ringStart
                            ),
                            center = center
                        ),
                        startAngle = 0f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        style = stroke
                    )
                }

                val radius = size.width / 2
                val top = Offset(center.x, center.y - radius)

                val tipAngle = 360f * animatedProgress

                rotate(degrees = tipAngle - 4f) {
                    drawFootprint(
                        at = Offset(
                            top.x,
                            top.y - 2.5.dp.toPx()
                        ),
                        color = footprintColor
                    )
                }

                if (animatedProgress > 0.05f) {
                    rotate(degrees = tipAngle - 15f) {
                        drawFootprint(
                            at = Offset(
                                top.x,
                                top.y + 2.5.dp.toPx()
                            ),
                            color = footprintColor
                        )
                    }
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

private fun DrawScope.drawFootprint(
    at: Offset,
    color: Color
) {
    val footWidth = 10.dp.toPx()
    val footHeight = 6.dp.toPx()

    drawOval(
        color = color,
        topLeft = Offset(
            at.x - footWidth / 2,
            at.y - footHeight / 2
        ),
        size = Size(footWidth, footHeight)
    )

    val toeRadius = 1.2.dp.toPx()

    drawCircle(
        color = color,
        radius = toeRadius,
        center = Offset(at.x + 7.dp.toPx(), at.y - 2.dp.toPx())
    )

    drawCircle(
        color = color,
        radius = toeRadius,
        center = Offset(at.x + 9.dp.toPx(), at.y)
    )

    drawCircle(
        color = color,
        radius = toeRadius,
        center = Offset(at.x + 7.dp.toPx(), at.y + 2.dp.toPx())
    )
}

@Composable
fun SleepDial(
    fill: Float,
    modifier: Modifier = Modifier
) {
    val f = fill.coerceIn(0f, 1f)

    val isDark = isSystemInDarkTheme()

    val trackColor = if (isDark) {
        SleepTextDark.copy(alpha = 0.25f)
    } else {
        SleepTextLight.copy(alpha = 0.25f)
    }

    val moonColor = if (isDark) {
        SleepTextDark
    } else {
        SleepTextLight
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Half-circle dial
        Canvas(
            modifier = Modifier.size(
                width = 56.dp,
                height = 30.dp
            )
        ) {
            val strokeWidth = 5.dp.toPx()
            val side = size.width - strokeWidth
            val radius = side / 2

            val arcCenter = Offset(
                size.width / 2,
                strokeWidth / 2 + radius
            )

            val arcTopLeft = Offset(
                strokeWidth / 2,
                strokeWidth / 2
            )

            val arcSize = Size(side, side)

            val arcStyle = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )

            drawArc(
                color = trackColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = arcStyle
            )

            if (f > 0f) {
                drawArc(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            SoftViolet,
                            SunsetOrange
                        )
                    ),
                    startAngle = 180f,
                    sweepAngle = 180f * f,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcSize,
                    style = arcStyle
                )

                rotate(
                    degrees = 180f * f,
                    pivot = arcCenter
                ) {
                    val tipPosition = Offset(
                        arcCenter.x - radius,
                        arcCenter.y
                    )

                    drawCircle(
                        color = Color.White,
                        radius = 3.dp.toPx(),
                        center = tipPosition
                    )

                    drawCircle(
                        color = SunsetOrange,
                        radius = 3.dp.toPx(),
                        center = tipPosition,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
            }
        }

        // Moon and sun icons beneath the dial
        Row(
            modifier = Modifier.width(56.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_sleep),
                contentDescription = "Sleep start",
                modifier = Modifier.size(11.dp),
                tint = moonColor
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_sun),
                contentDescription = "Morning",
                modifier = Modifier.size(11.dp),
                tint = SunsetOrange
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
    modifier: Modifier = Modifier,
    topContent: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        gradientStart.copy(
                            alpha = if (isSystemInDarkTheme()) 0.20f else 0.14f
                        ),
                        gradientEnd.copy(
                            alpha = if (isSystemInDarkTheme()) 0.20f else 0.14f
                        )
                    )
                )
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        if (topContent != null) {
            topContent()
        } else {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
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
}

@Composable
fun WaterBottle(
    fill: Float,
    modifier: Modifier = Modifier
) {
    val outlineColor = if (isSystemInDarkTheme()) {
        WaterTextDark
    } else {
        WaterTextLight
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Bottle cap
        Box(
            modifier = Modifier
                .size(width = 10.dp, height = 6.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.outlineVariant)
        )

        // Bottle body
        Box(
            modifier = Modifier
                .size(width = 26.dp, height = 40.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    width = 1.5.dp,
                    color = outlineColor,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            // Water fills from the bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(fill.coerceIn(0f, 1f))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                BrightBlue,
                                DeepBlue
                            )
                        )
                    )
            )
        }
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

    var started by remember { mutableStateOf(false) }

    val animatedProgress by animateFloatAsState(
        targetValue = if (started) progress else 0f,
        animationSpec = tween(durationMillis = 700),
        label = "waterProgress"
    )

    LaunchedEffect(Unit) {
        started = true
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WaterBottle(fill = animatedProgress)

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Water: ${waterMl.coerceAtLeast(0)} of %,d ml".format(goal),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            LinearProgressIndicator(
                progress = { animatedProgress },
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
}

@Composable
fun RecentRecordRow(
    record: HealthRecord,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()

    val (icon, color) = when {
        record.steps > 0 -> R.drawable.ic_steps to
                if (isDark) RingMintDark else RingMintLight

        record.calories > 0 -> R.drawable.ic_calories to
                if (isDark) CaloriesTextDark else CaloriesTextLight

        record.waterMl > 0 -> R.drawable.ic_water to
                if (isDark) WaterTextDark else WaterTextLight

        record.sleepMinutes > 0 -> R.drawable.ic_sleep to
                if (isDark) SleepTextDark else SleepTextLight

        else -> R.drawable.ic_history to
                MaterialTheme.colorScheme.onSurfaceVariant
    }

    val recordedText = recordSummary(record)
    val dateText = "${dayLabel(record.date, LocalDate.now())} · ${record.time}"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = color
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = recordedText,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = dateText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = "View record",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun RecentRecordsCard(
    records: List<HealthRecord>,
    onRecordClick: (Long) -> Unit,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent records",
                style = MaterialTheme.typography.titleMedium
            )

            TextButton(
                onClick = onSeeAllClick
            ) {
                Text("See all")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (records.isEmpty()) {
            Text(
                text = "No records yet. Tap + to add one.",
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 16.dp
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                records.take(3).forEachIndexed { index, record ->

                    if (index > 0) {
                        HorizontalDivider(
                            modifier = Modifier.padding(
                                start = 60.dp,
                                end = 16.dp
                            ),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }

                    RecentRecordRow(
                        record = record,
                        onClick = { onRecordClick(record.id) }
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun StreakBarPreview() {
    HealthMateCoreTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StreakBar(streak = 0)
            StreakBar(streak = 4)
            StreakBar(streak = 10)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WaterBottlePreview() {
    HealthMateCoreTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WaterBottle(fill = 0f)
            WaterBottle(fill = 0.3f)
            WaterBottle(fill = 0.6f)
            WaterBottle(fill = 1f)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SleepDialPreview() {
    HealthMateCoreTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SleepDial(fill = 0f)
            SleepDial(fill = 0.5f)
            SleepDial(fill = 1f)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecentRecordsCardPreview() {
    HealthMateCoreTheme {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            RecentRecordsCard(
                records = listOf(
                    HealthRecord(
                        id = 1L,
                        date = LocalDate.now().toString(),
                        time = "14:20",
                        steps = 8000,
                        calories = 300,
                        waterMl = 0,
                        sleepMinutes = 0,
                        note = null
                    ),
                    HealthRecord(
                        id = 2L,
                        date = LocalDate.now().toString(),
                        time = "11:45",
                        steps = 0,
                        calories = 0,
                        waterMl = 1250,
                        sleepMinutes = 0,
                        note = null
                    ),
                    HealthRecord(
                        id = 3L,
                        date = LocalDate.now().minusDays(1).toString(),
                        time = "08:30",
                        steps = 0,
                        calories = 0,
                        waterMl = 0,
                        sleepMinutes = 450,
                        note = null
                    )
                ),
                onRecordClick = {},
                onSeeAllClick = {}
            )
        }
    }
}