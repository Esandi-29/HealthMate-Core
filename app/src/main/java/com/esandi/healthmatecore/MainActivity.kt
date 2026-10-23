package com.esandi.healthmatecore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.esandi.healthmatecore.data.DailyTotals
import com.esandi.healthmatecore.data.HealthMateDatabase
import com.esandi.healthmatecore.data.HealthRecord
import com.esandi.healthmatecore.data.HealthRecordRepository
import com.esandi.healthmatecore.records.RecordListViewModel
import com.esandi.healthmatecore.ui.theme.HealthMateCoreTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.text.style.TextAlign
import java.time.Instant
import java.time.ZoneOffset

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = HealthMateDatabase.getDatabase(this)
        val repository = HealthRecordRepository(database.healthRecordDao())

        lifecycleScope.launch {
            repository.addSampleDataIfEmpty()
        }

        setContent {
            val viewModel: RecordListViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        RecordListViewModel(repository)
                    }
                }
            )

            val records by viewModel.records.collectAsState()
            val todayTotals by viewModel.todayTotals.collectAsState()
            val dateFilter by viewModel.dateFilter.collectAsState()

            HealthMateCoreTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "records"
                ) {
                    composable("records") {

                        RecordsScreen(
                            records = records,
                            onAddClick = {
                                navController.navigate("add")
                            },
                            onDelete = { record ->
                                viewModel.deleteRecord(record)
                            },
                            onEdit = { record ->
                                navController.navigate("edit/${record.id}")
                            },
                            totals = todayTotals,
                            selectedDate = dateFilter,
                            onDatePicked = { date ->
                                viewModel.setDateFilter(date)
                            },
                            onClearDateFilter = {
                                viewModel.clearDateFilter()
                            }
                        )
                    }

                    composable("add") {
                        AddRecordScreen(
                            onSave = { record ->
                                viewModel.addRecord(record)
                                navController.popBackStack()
                            },
                            onCancel = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable("edit/{recordId}") { backStackEntry ->
                        val recordId = backStackEntry.arguments
                            ?.getString("recordId")
                            ?.toLongOrNull()

                        val record = records.find { it.id == recordId }

                        if (record != null) {
                            EditRecordScreen(
                                record = record,
                                onSave = { updatedRecord ->
                                    viewModel.updateRecord(updatedRecord)
                                    navController.popBackStack()
                                },
                                onCancel = {
                                    navController.popBackStack()
                                }
                            )
                        } else {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text("Record not found")

                                TextButton(
                                    onClick = {
                                        navController.popBackStack()
                                    }
                                ) {
                                    Text("Back")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddRecordForm(
    onSave: (HealthRecord) -> Unit,
    modifier: Modifier = Modifier,
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

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        OutlinedTextField(
            value = stepsText,
            onValueChange = { stepsText = it },
            label = { Text("Steps") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

        OutlinedTextField(
            value = caloriesText,
            onValueChange = { caloriesText = it },
            label = { Text("Calories burned") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

        OutlinedTextField(
            value = waterText,
            onValueChange = { waterText = it },
            label = { Text("Water (ml)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

        OutlinedTextField(
            value = sleepText,
            onValueChange = { sleepText = it },
            label = { Text("Sleep (minutes)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

        Button(
            onClick = {
                val steps = stepsText.toIntOrNull() ?: 0
                val calories = caloriesText.toIntOrNull() ?: 0
                val water = waterText.toIntOrNull() ?: 0
                val sleep = sleepText.toIntOrNull() ?: 0

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
                }
            }
        ) {
            Text("Save")
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
fun RecordRow(
    record: HealthRecord,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val recordedText = when {
        record.steps > 0 -> "${record.steps} steps"
        record.calories > 0 -> "${record.calories} calories burned"
        record.waterMl > 0 -> "${record.waterMl} ml water"
        record.sleepMinutes > 0 ->
            "${record.sleepMinutes / 60}h ${record.sleepMinutes % 60}m sleep"

        !record.note.isNullOrBlank() -> record.note
        else -> "No health data recorded"
    }

    Row(
        modifier = Modifier.padding(
            horizontal = 16.dp,
            vertical = 8.dp
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "${record.date} • ${record.time}",
                fontSize = 16.sp
            )

            Text(
                text = recordedText ?: "No health data recorded",
                fontSize = 14.sp
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

@Composable
fun RecordList(
    records: List<HealthRecord>,
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
        items(records) { record ->
            RecordRow(
                record = record,
                onEditClick = {
                    onEdit(record)
                },
                onDeleteClick = {
                    recordToDelete = record
                }
            )
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
fun StepCounter(modifier: Modifier = Modifier) {
    var steps by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text(
            text = "Steps today: $steps",
            fontSize = 24.sp
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { steps += 500 }
            ) {
                Text("Add 500 steps")
            }

            Button(
                onClick = { steps = 0 }
            ) {
                Text("Reset")
            }
        }
    }

}

@Composable
fun WelcomeHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text(
            text = "HealthMate",
            fontSize = 32.sp
        )

        Text(
            text = "Welcome back, Esandi",
            fontSize = 18.sp
        )
    }
}

@Composable
fun RecordsScreen(
    records: List<HealthRecord>,
    onAddClick: () -> Unit,
    onDelete: (HealthRecord) -> Unit,
    onEdit: (HealthRecord) -> Unit,
    totals: DailyTotals,
    selectedDate: String?,
    onDatePicked: (String) -> Unit,
    onClearDateFilter: () -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick
            ) {
                Text("+")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            WelcomeHeader()

            TodaySummary(totals = totals)

            DateFilterBar(
                selectedDate = selectedDate,
                onDatePicked = onDatePicked,
                onClear = onClearDateFilter
            )

            if (records.isEmpty() && selectedDate != null) {
                Text(
                    text = "No records on this date",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .wrapContentHeight(Alignment.CenterVertically)
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                RecordList(
                    records = records,
                    onDelete = onDelete,
                    onEdit = onEdit,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun AddRecordScreen(
    onSave: (HealthRecord) -> Unit,
    onCancel: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = "Add record",
                fontSize = 24.sp,
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
}

@Composable
fun EditRecordScreen(
    record: HealthRecord,
    onSave: (HealthRecord) -> Unit,
    onCancel: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = "Edit record",
                fontSize = 24.sp,
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
}

@Composable
fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = value,
                fontSize = 20.sp
            )
            Text(
                text = label,
                fontSize = 12.sp
            )
        }
    }
}


@Composable
fun TodaySummary(totals: DailyTotals) {
    val sleepHours = totals.sleepMinutes / 60
    val sleepRemainder = totals.sleepMinutes % 60

    Column(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Today's Summary",
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                value = "%,d".format(totals.steps),
                label = "Steps",
                modifier = Modifier.weight(1f)
            )

            StatCard(
                value = "%,d".format(totals.calories),
                label = "Calories",
                modifier = Modifier.weight(1f)
            )

            StatCard(
                value = "${totals.waterMl} ml",
                label = "Water",
                modifier = Modifier.weight(1f)
            )

            StatCard(
                value = "${sleepHours}h ${sleepRemainder}m",
                label = "Sleep",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeHeaderPreview() {
    WelcomeHeader()
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
