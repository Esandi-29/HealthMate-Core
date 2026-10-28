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
import com.esandi.healthmatecore.ui.theme.ErrorRed
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters
import androidx.compose.material3.LinearProgressIndicator
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.horizontalScroll
import com.esandi.healthmatecore.records.DateRange
import com.esandi.healthmatecore.records.MetricFilter
import com.esandi.healthmatecore.records.dayLabel
import com.esandi.healthmatecore.records.filterRecords
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Slider
import kotlin.math.roundToInt
import androidx.compose.material3.AssistChip
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import com.esandi.healthmatecore.records.sleepMinutesBetween
import androidx.compose.ui.text.font.FontWeight
import com.esandi.healthmatecore.ui.theme.Roboto
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.FilterChipDefaults
import com.esandi.healthmatecore.ui.theme.RingLimeDark
import com.esandi.healthmatecore.ui.theme.RingLimeLight
import com.esandi.healthmatecore.ui.theme.RingMintLight
import com.esandi.healthmatecore.ui.theme.RingMintDark
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.rotate
import com.esandi.healthmatecore.ui.theme.CaloriesTextLight
import com.esandi.healthmatecore.ui.theme.SunsetOrange
import com.esandi.healthmatecore.ui.theme.CaloriesTextDark
import com.esandi.healthmatecore.ui.theme.WaterTextLight
import com.esandi.healthmatecore.ui.theme.WaterTextDark
import com.esandi.healthmatecore.ui.theme.SleepTextLight
import com.esandi.healthmatecore.ui.theme.SleepTextDark
import com.esandi.healthmatecore.ui.theme.BrightBlue
import com.esandi.healthmatecore.ui.theme.DeepBlue
import com.esandi.healthmatecore.ui.theme.SoftViolet
import com.esandi.healthmatecore.ui.theme.DeepPurple
import com.esandi.healthmatecore.ui.theme.SuccessGreen

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
            text = "Welcome back, Esandi",
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = Roboto,
                fontWeight = FontWeight.Normal
            )
        )
    }
}

@Composable
fun RecordsScreen(
    records: List<HealthRecord>,
    totals: DailyTotals,
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

        TodaySummary(totals = totals)

        LatestRecords(records = records)


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
                goal = 8000
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
            goal = 2000
        )

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
fun DetailLine(
    icon: Int,
    color: Color,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = color
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun RecordDetailsScreen(
    record: HealthRecord,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirmation by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp)
    ) {
        TextButton(
            onClick = onBack,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text("Back")
        }

        Text(
            text = "${record.date} • ${record.time}",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            if (record.steps > 0) {
                DetailLine(
                    icon = R.drawable.ic_progress,
                    color = MaterialTheme.colorScheme.primary,
                    label = "Steps",
                    value = "%,d".format(record.steps)
                )
            }

            if (record.calories > 0) {
                DetailLine(
                    icon = R.drawable.ic_calories,
                    color = ErrorRed,
                    label = "Calories",
                    value = "%,d".format(record.calories)
                )
            }

            if (record.waterMl > 0) {
                DetailLine(
                    icon = R.drawable.ic_water,
                    color = MaterialTheme.colorScheme.primary,
                    label = "Water",
                    value = "${record.waterMl} ml"
                )
            }

            if (record.sleepMinutes > 0) {
                val hours = record.sleepMinutes / 60
                val minutes = record.sleepMinutes % 60

                DetailLine(
                    icon = R.drawable.ic_sleep,
                    color = MaterialTheme.colorScheme.secondary,
                    label = "Sleep",
                    value = "${hours}h ${minutes}m"
                )
            }
        }

        if (!record.note.isNullOrBlank()) {
            Text(
                text = "Note",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 4.dp
                )
            )

            Text(
                text = record.note.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onEdit,
                modifier = Modifier.weight(1f)
            ) {
                Text("Edit")
            }

            Button(
                onClick = {
                    showDeleteConfirmation = true
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmation = false
            },
            title = {
                Text("Delete record?")
            },
            text = {
                Text("You can undo this right after.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onDelete()
                    }
                ) {
                    Text(
                        "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Cancel")
                }
            }
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
