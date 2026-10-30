package com.esandi.healthmatecore.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esandi.healthmatecore.data.Goals
import com.esandi.healthmatecore.data.GoalsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GoalsViewModel(
    private val repository: GoalsRepository
) : ViewModel() {

    val goals: StateFlow<Goals> = repository.getGoals()
        .map { it ?: Goals() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Goals()
        )

    fun saveGoals(goals: Goals) {
        viewModelScope.launch {
            repository.saveGoals(goals)
        }
    }
}