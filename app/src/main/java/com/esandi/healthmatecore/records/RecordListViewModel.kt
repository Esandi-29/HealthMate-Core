package com.esandi.healthmatecore.records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esandi.healthmatecore.data.DailyTotals
import com.esandi.healthmatecore.data.HealthRecord
import com.esandi.healthmatecore.data.HealthRecordRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

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
