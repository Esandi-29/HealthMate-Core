package com.esandi.healthmatecore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.esandi.healthmatecore.data.DailyTotals
import com.esandi.healthmatecore.data.HealthMateDatabase
import com.esandi.healthmatecore.data.HealthRecord
import com.esandi.healthmatecore.data.HealthRecordRepository
import com.esandi.healthmatecore.records.RecordListViewModel
import com.esandi.healthmatecore.records.RecordValidator
import com.esandi.healthmatecore.ui.theme.HealthMateCoreTheme
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Color
import com.esandi.healthmatecore.ui.theme.Coral
import androidx.compose.foundation.clickable
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters

data class BottomTab(
    val route: String,
    val label: String,
    val icon: Int
)

val bottomTabs = listOf(
    BottomTab("home", "Home", R.drawable.ic_home),
    BottomTab("history", "History", R.drawable.ic_history),
    BottomTab("add", "Add", R.drawable.ic_add),
    BottomTab("progress", "Progress", R.drawable.ic_progress),
    BottomTab("profile", "Profile", R.drawable.ic_profile)
)

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
                val currentRoute =
                    navController.currentBackStackEntryAsState().value
                        ?.destination?.route
                val showBottomBar = currentRoute in listOf(
                    "home",
                    "history",
                    "progress",
                    "profile"
                )

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar {
                                bottomTabs.forEach { tab ->
                                    if (tab.route == "add") {
                                        NavigationBarItem(
                                            selected = false,
                                            onClick = {
                                                navController.navigate("add")
                                            },
                                            icon = {
                                                Box(
                                                    modifier = Modifier
                                                        .size(48.dp)
                                                        .clip(CircleShape)
                                                        .background(MaterialTheme.colorScheme.primary),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        painter = painterResource(id = tab.icon),
                                                        contentDescription = tab.label,
                                                        tint = MaterialTheme.colorScheme.onPrimary
                                                    )
                                                }
                                            }
                                        )
                                    } else {
                                        NavigationBarItem(
                                            selected = currentRoute == tab.route,
                                            onClick = {
                                                navController.navigate(tab.route) {
                                                    popUpTo("home") {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            },
                                            icon = {
                                                Icon(
                                                    painter = painterResource(id = tab.icon),
                                                    contentDescription = tab.label
                                                )
                                            },
                                            label = {
                                                Text(tab.label)
                                            },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {

                            RecordsScreen(
                                records = records,
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

                        composable("history") {
                            PlaceholderScreen(
                                title = "History",
                                message = "All your past records will appear here."
                            )
                        }

                        composable("progress") {
                            PlaceholderScreen(
                                title = "Progress",
                                message = "Your weekly charts will appear here."
                            )
                        }

                        composable("profile") {
                            PlaceholderScreen(
                                title = "Profile",
                                message = "Your goals and settings will appear here."
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

    Column(
        modifier = modifier.padding(16.dp)
    ) {
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
        itemsIndexed(records) { index, record ->
            val isFirst = index == 0
            val isLast = index == records.lastIndex

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
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Welcome back, Esandi",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun RecordsScreen(
    records: List<HealthRecord>,
    onDelete: (HealthRecord) -> Unit,
    onEdit: (HealthRecord) -> Unit,
    totals: DailyTotals,
    selectedDate: String?,
    onDatePicked: (String) -> Unit,
    onClearDateFilter: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        WelcomeHeader()

        WeekStrip(
            records = records,
            onDayClick = onDatePicked
        )

        TodaySummary(totals = totals)

        DateFilterBar(
            selectedDate = selectedDate,
            onDatePicked = onDatePicked,
            onClear = onClearDateFilter
        )


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
            style = MaterialTheme.typography.headlineSmall,
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
                goal = 8000
            )
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            MetricTile(
                value = "%,d".format(totals.calories),
                label = "Calories",
                icon = R.drawable.ic_calories,
                color = Coral,
                modifier = Modifier.weight(1f)
            )

            MetricTile(
                value = "${totals.waterMl} ml",
                label = "Water",
                icon = R.drawable.ic_water,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )

            MetricTile(
                value = "${sleepHours}h ${sleepRemainder}m",
                label = "Sleep",
                icon = R.drawable.ic_sleep,
                color = MaterialTheme.colorScheme.secondary,
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

@Composable
fun PlaceholderScreen(
    title: String,
    message: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
    val ringColor = MaterialTheme.colorScheme.primary

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
                drawArc(
                    color = ringColor,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = stroke
                )
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.12f))
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
                                MaterialTheme.colorScheme.primary
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
                            MaterialTheme.colorScheme.onPrimary
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
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.Transparent
                            }
                        )
                )
            }
        }
    }
}

