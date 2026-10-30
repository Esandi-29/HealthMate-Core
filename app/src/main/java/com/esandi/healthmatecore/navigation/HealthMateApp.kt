package com.esandi.healthmatecore.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.esandi.healthmatecore.R
import com.esandi.healthmatecore.data.Goals
import com.esandi.healthmatecore.data.HealthRecord
import com.esandi.healthmatecore.details.RecordDetailsScreen
import com.esandi.healthmatecore.history.HistoryScreen
import com.esandi.healthmatecore.home.RecordsScreen
import com.esandi.healthmatecore.profile.GoalsScreen
import com.esandi.healthmatecore.profile.GoalsViewModel
import com.esandi.healthmatecore.profile.ProfileScreen
import com.esandi.healthmatecore.progress.ProgressScreen
import com.esandi.healthmatecore.recordform.AddRecordScreen
import com.esandi.healthmatecore.recordform.EditRecordScreen
import com.esandi.healthmatecore.records.RecordListViewModel
import kotlinx.coroutines.launch

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
fun HealthMateApp(
    viewModel: RecordListViewModel,
    goalsViewModel: GoalsViewModel
) {

    val records by viewModel.records.collectAsState()
    val todayTotals by viewModel.todayTotals.collectAsState()
    val dateFilter by viewModel.dateFilter.collectAsState()
    val allRecords by viewModel.allRecords.collectAsState()
    val weekTotals by viewModel.weekTotals.collectAsState()
    val weekStart by viewModel.selectedWeekStart.collectAsState()
    val monthTotals by viewModel.monthTotals.collectAsState()
    val monthStart by viewModel.selectedMonthStart.collectAsState()
    val dayRecords by viewModel.dayRecords.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()
    val goals by goalsViewModel.goals.collectAsState()

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
                    goals = goals,
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
                ProgressScreen(
                    weekTotals = weekTotals,
                    weekStart = weekStart,
                    onPreviousWeek = viewModel::previousWeek,
                    onNextWeek = viewModel::nextWeek,
                    monthTotals = monthTotals,
                    monthStart = monthStart,
                    onPreviousMonth = viewModel::previousMonth,
                    onNextMonth = viewModel::nextMonth,
                    dayRecords = dayRecords,
                    selectedDay = selectedDay,
                    onPreviousDay = viewModel::previousDay,
                    onNextDay = viewModel::nextDay,
                    goals = goals
                )
            }

            composable("profile") {
                ProfileScreen(
                    name = "Esandi",
                    onOptionClick = { route ->
                        navController.navigate(route)
                    }
                )
            }

            composable("personal_info") {
                PlaceholderScreen(
                    title = "Personal Information",
                    message = "Coming soon."
                )
            }

            composable("goals") {
                GoalsScreen(
                    initialSteps = goals.steps,
                    initialCalories = goals.calories,
                    initialWater = goals.waterMl,
                    initialSleepMinutes = goals.sleepMinutes,
                    initialActiveDays = goals.activeDays,
                    onSave = { steps, calories, water, sleepMinutes, activeDays ->
                        goalsViewModel.saveGoals(
                            Goals(
                                steps = steps,
                                calories = calories,
                                waterMl = water,
                                sleepMinutes = sleepMinutes,
                                activeDays = activeDays
                            )
                        )
                        scope.launch {
                            snackbarHostState.showSnackbar("Goals saved")
                        }
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable("notifications") {
                PlaceholderScreen(
                    title = "Notifications",
                    message = "Coming soon."
                )
            }

            composable("app_settings") {
                PlaceholderScreen(
                    title = "App Settings",
                    message = "Coming soon."
                )
            }

            composable("help") {
                PlaceholderScreen(
                    title = "Help and Support",
                    message = "Coming soon."
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

