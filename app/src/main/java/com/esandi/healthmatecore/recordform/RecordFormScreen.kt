package com.esandi.healthmatecore.recordform

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.esandi.healthmatecore.data.HealthRecord
import com.esandi.healthmatecore.records.RecordValidator
import com.esandi.healthmatecore.records.sleepMinutesBetween
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun AddRecordForm(
    onSave: (HealthRecord) -> Unit,
    modifier: Modifier = Modifier.Companion,
    initialRecord: HealthRecord? = null
) {
    var stepsText by remember(initialRecord?.id) {
        mutableStateOf(numberText(initialRecord?.steps))
    }
    var caloriesText by remember(initialRecord?.id) {
        mutableStateOf(numberText(initialRecord?.calories))
    }
    var waterText by remember(initialRecord?.id) {
        mutableStateOf(numberText(initialRecord?.waterMl))
    }
    var sleepText by remember(initialRecord?.id) {
        mutableStateOf(numberText(initialRecord?.sleepMinutes))

    }
    var bedTime by remember(initialRecord?.id) {
        mutableStateOf<LocalTime?>(null)
    }

    var wakeTime by remember(initialRecord?.id) {
        mutableStateOf<LocalTime?>(null)
    }

    var showBedPicker by remember(initialRecord?.id) {
        mutableStateOf(false)
    }

    var showWakePicker by remember(initialRecord?.id) {
        mutableStateOf(false)
    }

    var stepsError by remember(initialRecord?.id) {
        mutableStateOf<String?>(null)
    }
    var caloriesError by remember(initialRecord?.id) {
        mutableStateOf<String?>(null)
    }
    var waterError by remember(initialRecord?.id) {
        mutableStateOf<String?>(null)
    }
    var sleepError by remember(initialRecord?.id) {
        mutableStateOf<String?>(null)
    }
    var formError by remember(initialRecord?.id) {
        mutableStateOf<String?>(null)
    }

    var selectedTab by remember(initialRecord?.id) {
        mutableStateOf(
            when {
                (initialRecord?.calories ?: 0) > 0 -> RecordTab.CALORIES
                (initialRecord?.waterMl ?: 0) > 0 -> RecordTab.WATER
                (initialRecord?.sleepMinutes ?: 0) > 0 -> RecordTab.SLEEP
                else -> RecordTab.STEPS
            }
        )
    }

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            RecordTab.entries.forEachIndexed { index, option ->
                SegmentedButton(
                    selected = selectedTab == option,
                    onClick = { selectedTab = option },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = RecordTab.entries.size
                    )
                ) {
                    Text(option.label)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        when (selectedTab) {
            RecordTab.STEPS -> {
                OutlinedTextField(
                    value = stepsText,
                    onValueChange = {
                        stepsText = it
                        stepsError = null
                        formError = null
                    },
                    label = { Text("Steps") },
                    isError = stepsError != null,
                    supportingText = {
                        stepsError?.let { Text(it) }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Slider(
                    value = (stepsText.toIntOrNull() ?: 0)
                        .coerceIn(0, 30_000)
                        .toFloat(),
                    onValueChange = { value ->
                        stepsText = ((value / 100).roundToInt() * 100).toString()
                        stepsError = null
                        formError = null
                    },
                    valueRange = 0f..30_000f,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            RecordTab.CALORIES -> {
                OutlinedTextField(
                    value = caloriesText,
                    onValueChange = {
                        caloriesText = it
                        caloriesError = null
                        formError = null
                    },
                    label = { Text("Calories burned") },
                    isError = caloriesError != null,
                    supportingText = {
                        caloriesError?.let { Text(it) }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Slider(
                    value = (caloriesText.toIntOrNull() ?: 0)
                        .coerceIn(0, 2_000)
                        .toFloat(),
                    onValueChange = { value ->
                        caloriesText = ((value / 10).roundToInt() * 10).toString()
                        caloriesError = null
                        formError = null
                    },
                    valueRange = 0f..2_000f,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            RecordTab.WATER -> {
                OutlinedTextField(
                    value = waterText,
                    onValueChange = {
                        waterText = it
                        waterError = null
                        formError = null
                    },
                    label = { Text("Water (ml)") },
                    isError = waterError != null,
                    supportingText = {
                        waterError?.let { Text(it) }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(250, 500, 750).forEach { amount ->
                        AssistChip(
                            onClick = {
                                val current = waterText.toIntOrNull() ?: 0
                                waterText = (current + amount).toString()
                                waterError = null
                                formError = null
                            },
                            label = { Text("+$amount ml") }
                        )
                    }

                    AssistChip(
                        onClick = {
                            waterText = ""
                            waterError = null
                            formError = null
                        },
                        label = { Text("Clear") }
                    )
                }
            }

            RecordTab.SLEEP -> {
                OutlinedTextField(
                    value = sleepText,
                    onValueChange = {
                        sleepText = it
                        sleepError = null
                        formError = null
                    },
                    label = { Text("Sleep (minutes)") },
                    isError = sleepError != null,
                    supportingText = {
                        sleepError?.let { Text(it) }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showBedPicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Bed ${bedTime?.toString() ?: "--:--"}")
                    }

                    OutlinedButton(
                        onClick = { showWakePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Wake ${wakeTime?.toString() ?: "--:--"}")
                    }
                }
            }
        }

        formError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Button(
            onClick = {
                stepsError = RecordValidator.validateSteps(stepsText)
                caloriesError = RecordValidator.validateCalories(caloriesText)
                waterError = RecordValidator.validateWater(waterText)
                sleepError = RecordValidator.validateSleep(sleepText)

                val fieldsAreValid =
                    stepsError == null &&
                            caloriesError == null &&
                            waterError == null &&
                            sleepError == null

                if (!fieldsAreValid) {
                    selectedTab = when {
                        stepsError != null -> RecordTab.STEPS
                        caloriesError != null -> RecordTab.CALORIES
                        waterError != null -> RecordTab.WATER
                        else -> RecordTab.SLEEP
                    }
                    formError = null
                    return@Button
                }

                val steps = stepsText.toIntOrNull() ?: 0
                val calories = caloriesText.toIntOrNull() ?: 0
                val water = waterText.toIntOrNull() ?: 0
                val sleep = sleepText.toIntOrNull() ?: 0

                formError = RecordValidator.validateAtLeastOne(
                    steps = steps,
                    calories = calories,
                    water = water,
                    sleep = sleep
                )

                if (formError != null) {
                    return@Button
                }

                val record = if (initialRecord != null) {
                    initialRecord.copy(
                        steps = steps,
                        calories = calories,
                        waterMl = water,
                        sleepMinutes = sleep
                    )
                } else {
                    HealthRecord(
                        date = LocalDate.now().toString(),
                        time = LocalTime.now().format(
                            DateTimeFormatter.ofPattern("HH:mm")
                        ),
                        steps = steps,
                        calories = calories,
                        waterMl = water,
                        sleepMinutes = sleep
                    )
                }

                onSave(record)

                if (initialRecord == null) {
                    stepsText = ""
                    caloriesText = ""
                    waterText = ""
                    sleepText = ""

                    stepsError = null
                    caloriesError = null
                    waterError = null
                    sleepError = null
                    formError = null

                    bedTime = null
                    wakeTime = null
                }
            }
        ) {
            Text("Save")
        }
        if (showBedPicker) {
            TimePickerDialog(
                title = "Bed time",
                initialTime = bedTime,
                onConfirm = { time ->
                    bedTime = time
                    showBedPicker = false

                    wakeTime?.let { wake ->
                        sleepText = sleepMinutesBetween(time, wake).toString()
                        sleepError = null
                        formError = null
                    }
                },
                onDismiss = {
                    showBedPicker = false
                }
            )
        }
        if (showWakePicker) {
            TimePickerDialog(
                title = "Wake time",
                initialTime = wakeTime ?: LocalTime.of(7, 0),
                onConfirm = { time ->
                    wakeTime = time
                    showWakePicker = false

                    bedTime?.let { bed ->
                        sleepText = sleepMinutesBetween(bed, time).toString()
                        sleepError = null
                        formError = null
                    }
                },
                onDismiss = {
                    showWakePicker = false
                }
            )
        }
    }
}

fun numberText(value: Int?): String {
    return if (value != null && value > 0) {
        value.toString()
    } else {
        ""
    }
}

@Composable
fun AddRecordScreen(
    onSave: (HealthRecord) -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Add record",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        AddRecordForm(
            onSave = onSave
        )

        TextButton(
            onClick = onCancel,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text("Cancel")
        }
    }
}

@Composable
fun EditRecordScreen(
    record: HealthRecord,
    onSave: (HealthRecord) -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Edit record",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        AddRecordForm(
            onSave = onSave,
            initialRecord = record
        )

        TextButton(
            onClick = onCancel,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text("Cancel")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    title: String,
    initialTime: LocalTime?,
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = initialTime?.hour ?: 22,
        initialMinute = initialTime?.minute ?: 0,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title)
        },
        text = {
            TimePicker(state = state)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        LocalTime.of(
                            state.hour,
                            state.minute
                        )
                    )
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

enum class RecordTab(val label: String) {
    STEPS("Steps"),
    CALORIES("Calories"),
    WATER("Water"),
    SLEEP("Sleep")
}