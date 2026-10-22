package com.esandi.healthmatecore.records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esandi.healthmatecore.data.HealthRecord
import com.esandi.healthmatecore.data.HealthRecordRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecordListViewModel(
    private val repository: HealthRecordRepository
) : ViewModel() {

    val records: StateFlow<List<HealthRecord>> =
        repository.getAllRecords().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
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
}
