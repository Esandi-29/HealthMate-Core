package com.esandi.healthmatecore.records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esandi.healthmatecore.data.DailyTotals
import com.esandi.healthmatecore.data.DayTotals
import com.esandi.healthmatecore.data.HealthRecord
import com.esandi.healthmatecore.data.HealthRecordRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@OptIn(ExperimentalCoroutinesApi::class)
class RecordListViewModel(
    private val repository: HealthRecordRepository
) : ViewModel() {

    private val selectedDate = MutableStateFlow<String?>(null)

    val dateFilter: StateFlow<String?> = selectedDate

    val records: StateFlow<List<HealthRecord>> = selectedDate
        .flatMapLatest { date ->
            if (date == null) {
                repository.getAllRecords()
            } else {
                repository.getRecordsByDate(date)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val allRecords: StateFlow<List<HealthRecord>> =
        repository.getAllRecords()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun setDateFilter(date: String) {
        selectedDate.value = date
    }

    fun clearDateFilter() {
        selectedDate.value = null
    }

    val todayTotals: StateFlow<DailyTotals> =
        repository.getTotalsForDate(LocalDate.now().toString())
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = DailyTotals(
                    steps = 0,
                    calories = 0,
                    waterMl = 0,
                    sleepMinutes = 0
                )
            )

    private fun thisMonday(): LocalDate =
        LocalDate.now().with(
            TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
        )

    private val weekStart = MutableStateFlow(thisMonday())

    val selectedWeekStart: StateFlow<LocalDate> = weekStart

    val weekTotals: StateFlow<List<DayTotals>> = weekStart
        .flatMapLatest { start ->
            repository.getDailyTotalsBetween(
                start.toString(),
                start.plusDays(6).toString()
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun previousWeek() {
        weekStart.value = weekStart.value.minusWeeks(1)
    }

    fun nextWeek() {
        if (weekStart.value < thisMonday()) {
            weekStart.value = weekStart.value.plusWeeks(1)
        }
    }

    private fun thisMonthStart(): LocalDate =
        LocalDate.now().withDayOfMonth(1)

    private val monthStart = MutableStateFlow(thisMonthStart())

    val selectedMonthStart: StateFlow<LocalDate> = monthStart

    val monthTotals: StateFlow<List<DayTotals>> = monthStart
        .flatMapLatest { start ->
            repository.getDailyTotalsBetween(
                start.toString(),
                start.plusMonths(1).minusDays(1).toString()
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun previousMonth() {
        monthStart.value = monthStart.value.minusMonths(1)
    }

    fun nextMonth() {
        if (monthStart.value < thisMonthStart()) {
            monthStart.value = monthStart.value.plusMonths(1)
        }
    }

    fun addRecord(record: HealthRecord) {
        viewModelScope.launch {
            repository.addRecord(record)
        }
    }

    fun deleteRecord(record: HealthRecord) {
        viewModelScope.launch {
            repository.deleteRecord(record)
        }
    }

    fun updateRecord(record: HealthRecord) {
        viewModelScope.launch {
            repository.updateRecord(record)
        }
    }
}
