package com.esandi.healthmatecore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.esandi.healthmatecore.data.HealthMateDatabase
import com.esandi.healthmatecore.data.HealthRecord
import com.esandi.healthmatecore.data.HealthRecordRepository
import com.esandi.healthmatecore.details.RecordDetailsScreen
import com.esandi.healthmatecore.history.HistoryScreen
import com.esandi.healthmatecore.home.RecordsScreen
import com.esandi.healthmatecore.records.RecordListViewModel
import com.esandi.healthmatecore.records.RecordValidator
import com.esandi.healthmatecore.records.sleepMinutesBetween
import com.esandi.healthmatecore.ui.theme.HealthMateCoreTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

data class BottomTab(
    val route: String,
    val label: String,
    val icon: Int
)

enum class RecordTab(val label: String) {
    STEPS("Steps"),
    CALORIES("Calories"),
    WATER("Water"),
    SLEEP("Sleep")
}

val bottomTabs = listOf(
    BottomTab("home", "Home", R.drawable.ic_home),
    BottomTab("history", "History", R.drawable.ic_history),
    BottomTab("add", "Add", R.drawable.ic_add),
    BottomTab("progress", "Progress", R.drawable.ic_progress),
    BottomTab("profile", "Profile", R.drawable.ic_profile)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
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
            val allRecords by viewModel.allRecords.collectAsState()

            HealthMateCoreTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                fun deleteWithUndo(record: HealthRecord) {
                    viewModel.deleteRecord(record)

                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Record deleted",
                            actionLabel = "Undo",
                            duration = SnackbarDuration.Short
                        )

                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.addRecord(record)
                        }
                    }
                }

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
                    snackbarHost = {
                        SnackbarHost(
                            hostState = snackbarHostState
                        ) { data ->
                            Snackbar(
                                snackbarData = data,
                                shape = RoundedCornerShape(16.dp),
                                containerColor = MaterialTheme.colorScheme.inverseSurface,
                                contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                                actionColor = MaterialTheme.colorScheme.inversePrimary
                            )
                        }
                    },
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
                                records = allRecords,
                                totals = todayTotals,
                                onDatePicked = { date ->
                                    viewModel.setDateFilter(date)
                                    navController.navigate("history") {
                                        popUpTo("home") {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }

                        composable("history") {
                            HistoryScreen(
                                records = records,
                                selectedDate = dateFilter,
                                onDatePicked = { date ->
                                    viewModel.setDateFilter(date)
                                },
                                onClearDateFilter = {
                                    viewModel.clearDateFilter()
                                },
                                onEdit = { record ->
                                    navController.navigate("edit/${record.id}")
                                },
                                onDelete = { record ->
                                    deleteWithUndo(record)
                                },
                                onOpen = { record ->
                                    navController.navigate("details/${record.id}")
                                }
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

                                    scope.launch {
                                        snackbarHostState.showSnackbar("Record added")
                                    }

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

                            val record = allRecords.find { it.id == recordId }

                            if (record != null) {
                                EditRecordScreen(
                                    record = record,
                                    onSave = { updatedRecord ->
                                        viewModel.updateRecord(updatedRecord)

                                        scope.launch {
                                            snackbarHostState.showSnackbar("Record updated")
                                        }

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
                        composable("details/{recordId}") { entry ->
                            val recordId = entry.arguments
                                ?.getString("recordId")
                                ?.toLongOrNull()

                            val record = allRecords.firstOrNull { it.id == recordId }

                            if (record != null) {
                                RecordDetailsScreen(
                                    record = record,
                                    onBack = {
                                        navController.popBackStack()
                                    },
                                    onEdit = {
                                        navController.navigate("edit/${record.id}")
                                    },
                                    onDelete = {
                                        deleteWithUndo(record)
                                        navController.popBackStack()
                                    }
                                )
                            } else {
                                LaunchedEffect(Unit) {
                                    if (navController.currentBackStackEntry == entry) {
                                        navController.popBackStack()
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
