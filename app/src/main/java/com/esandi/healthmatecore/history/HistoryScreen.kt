package com.esandi.healthmatecore.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.esandi.healthmatecore.data.HealthRecord
import com.esandi.healthmatecore.records.DateRange
import com.esandi.healthmatecore.records.MetricFilter
import com.esandi.healthmatecore.records.dayLabel
import com.esandi.healthmatecore.records.filterRecords
import com.esandi.healthmatecore.ui.theme.BrightBlue
import com.esandi.healthmatecore.ui.theme.ErrorRed
import com.esandi.healthmatecore.ui.theme.SoftViolet
import com.esandi.healthmatecore.ui.theme.SuccessGreen
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun HistoryScreen(
    records: List<HealthRecord>,
    selectedDate: String?,
    onDatePicked: (String) -> Unit,
    onClearDateFilter: () -> Unit,
    onEdit: (HealthRecord) -> Unit,
    onDelete: (HealthRecord) -> Unit,
    onOpen: (HealthRecord) -> Unit
) {
    var range by rememberSaveable {
        mutableStateOf(DateRange.ALL)
    }

    var metric by rememberSaveable {
        mutableStateOf(MetricFilter.ALL)
    }

    val shownRecords = filterRecords(
        records = records,
        range = if (selectedDate != null) DateRange.ALL else range,
        metric = metric,
        today = LocalDate.now()
    )
    val faintOutline = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.16f)

    val solidBlueColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primary,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "History",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp)
        )

        DateFilterBar(
            selectedDate = selectedDate,
            onDatePicked = onDatePicked,
            onClear = onClearDateFilter
        )
        Column(
            modifier = Modifier.padding(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DateRange.entries.forEach { option ->
                    FilterChip(
                        selected = range == option,
                        onClick = { range = option },
                        enabled = selectedDate == null,
                        label = { Text(option.label) },
                        colors = solidBlueColors,
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = selectedDate == null,
                            selected = range == option,
                            borderColor = faintOutline,
                            selectedBorderColor = MaterialTheme.colorScheme.primary,
                            borderWidth = 0.5.dp
                        )
                    )
                }
            }

            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricFilter.entries.forEach { option ->
                    val metricColor = when (option) {
                        MetricFilter.STEPS -> SuccessGreen
                        MetricFilter.CALORIES -> ErrorRed
                        MetricFilter.WATER -> BrightBlue
                        MetricFilter.SLEEP -> SoftViolet
                        MetricFilter.ALL -> MaterialTheme.colorScheme.primary
                    }
                    val isAll = option == MetricFilter.ALL

                    FilterChip(
                        selected = metric == option,
                        onClick = { metric = option },
                        label = { Text(option.label) },
                        shape = CircleShape,
                        colors = if (isAll) {
                            solidBlueColors
                        } else {
                            FilterChipDefaults.filterChipColors(
                                selectedContainerColor = metricColor.copy(alpha = 0.22f),
                                selectedLabelColor = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = metric == option,
                            borderColor = faintOutline,
                            selectedBorderColor = if (isAll) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                metricColor.copy(alpha = 0.4f)
                            },
                            borderWidth = 0.5.dp,
                            selectedBorderWidth = 1.dp
                        )
                    )
                }
            }
        }

        if (records.isEmpty()) {
            if (selectedDate != null) {
                Text(
                    text = "No records on this date",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
            } else {
                Text(
                    text = "No records yet. Tap + to add your first one.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        } else if (shownRecords.isEmpty()) {
            Text(
                text = "No records match these filters.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
        } else {
            RecordList(
                records = shownRecords,
                onOpen = onOpen,
                onDelete = onDelete,
                onEdit = onEdit,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun RecordList(
    records: List<HealthRecord>,
    onOpen: (HealthRecord) -> Unit,
    onDelete: (HealthRecord) -> Unit,
    onEdit: (HealthRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    var recordToDelete by remember {
        mutableStateOf<HealthRecord?>(null)
    }

    LazyColumn(
        modifier = modifier
    ) {
        val byDay = records.groupBy { it.date }

        byDay.forEach { (date, dayRecords) ->

            item(key = "header-$date") {
                Text(
                    text = dayLabel(date, LocalDate.now()),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(
                        start = 32.dp,
                        top = 16.dp,
                        bottom = 6.dp
                    )
                )
            }

            itemsIndexed(
                dayRecords,
                key = { _, record -> record.id }
            ) { index, record ->

                val isFirst = index == 0
                val isLast = index == dayRecords.lastIndex

                val shape = RoundedCornerShape(
                    topStart = if (isFirst) 16.dp else 0.dp,
                    topEnd = if (isFirst) 16.dp else 0.dp,
                    bottomStart = if (isLast) 16.dp else 0.dp,
                    bottomEnd = if (isLast) 16.dp else 0.dp
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .clip(shape)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    RecordRow(
                        record = record,
                        onClick = {
                            onOpen(record)
                        },
                        onEditClick = {
                            onEdit(record)
                        },
                        onDeleteClick = {
                            recordToDelete = record
                        }
                    )

                    if (!isLast) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
    }

    recordToDelete?.let { record ->
        AlertDialog(
            onDismissRequest = {
                recordToDelete = null
            },
            title = {
                Text("Delete record?")
            },
            text = {
                Text(
                    "The record from ${record.date} at ${record.time} will be deleted."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(record)
                        recordToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        recordToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun RecordRow(
    record: HealthRecord,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val parts = buildList {
        if (record.steps > 0) {
            add("%,d steps".format(record.steps))
        }

        if (record.calories > 0) {
            add("%,d kcal".format(record.calories))
        }

        if (record.waterMl > 0) {
            add("%,d ml water".format(record.waterMl))
        }

        if (record.sleepMinutes > 0) {
            val hours = record.sleepMinutes / 60
            val minutes = record.sleepMinutes % 60
            add("${hours}h ${minutes}m sleep")
        }
    }

    val recordedText = when {
        parts.isNotEmpty() -> parts.joinToString(" · ")
        !record.note.isNullOrBlank() -> record.note
        else -> "No health data recorded"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = record.time,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = recordedText,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        TextButton(
            onClick = onEditClick
        ) {
            Text("Edit")
        }

        TextButton(
            onClick = onDeleteClick
        ) {
            Text("Delete")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateFilterBar(
    selectedDate: String?,
    onDatePicked: (String) -> Unit,
    onClear: () -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }
    val pickerState = rememberDatePickerState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (selectedDate == null) {
                "All dates"
            } else {
                "Showing $selectedDate"
            },
            modifier = Modifier.weight(1f)
        )

        TextButton(onClick = { showPicker = true }) {
            Text("Pick date")
        }

        if (selectedDate != null) {
            TextButton(onClick = onClear) {
                Text("Clear")
            }
        }
    }

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { millis ->
                            val date = Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                                .toString()

                            onDatePicked(date)
                        }

                        showPicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}