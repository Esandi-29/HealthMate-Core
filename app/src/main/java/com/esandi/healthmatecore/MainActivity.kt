package com.esandi.healthmatecore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esandi.healthmatecore.data.HealthRecord
import com.esandi.healthmatecore.ui.theme.HealthMateCoreTheme
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.esandi.healthmatecore.data.HealthMateDatabase
import com.esandi.healthmatecore.data.HealthRecordRepository
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.initializer
import com.esandi.healthmatecore.records.RecordListViewModel
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.KeyboardType
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
            HealthMateCoreTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        WelcomeHeader()

                        AddRecordForm(
                            onSave = { record ->
                                viewModel.addRecord(record)
                            }
                        )

                        RecordList(records = records)
                    }
                }
            }
        }
    }
}
@Composable
fun AddRecordForm(
    onSave: (HealthRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    var stepsText by remember { mutableStateOf("") }
    var caloriesText by remember { mutableStateOf("") }
    var waterText by remember { mutableStateOf("") }
    var sleepText by remember { mutableStateOf("") }

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
                val record = HealthRecord(
                    date = LocalDate.now().toString(),
                    time = LocalTime.now().format(
                        DateTimeFormatter.ofPattern("HH:mm")
                    ),
                    steps = stepsText.toIntOrNull() ?: 0,
                    calories = caloriesText.toIntOrNull() ?: 0,
                    waterMl = waterText.toIntOrNull() ?: 0,
                    sleepMinutes = sleepText.toIntOrNull() ?: 0
                )

                onSave(record)

                stepsText = ""
                caloriesText = ""
                waterText = ""
                sleepText = ""
            }
        ) {
            Text("Save")
        }
    }
}
@Composable
fun RecordRow(record: HealthRecord) {
    val recordedText = when {
        record.steps > 0 -> "${record.steps} steps"
        record.calories > 0 -> "${record.calories} calories burned"
        record.waterMl > 0 -> "${record.waterMl} ml water"
        record.sleepMinutes > 0 ->
            "${record.sleepMinutes / 60}h ${record.sleepMinutes % 60}m sleep"

        !record.note.isNullOrBlank() -> record.note
        else -> "No health data recorded"
    }

    Column(
        modifier = Modifier.padding(
            horizontal = 16.dp,
            vertical = 8.dp
        )
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
}

@Composable
fun RecordList(
    records: List<HealthRecord>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(records) { record ->
            RecordRow(record = record)
        }
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

@Preview(showBackground = true)
@Composable
fun WelcomeHeaderPreview() {
    WelcomeHeader()
}