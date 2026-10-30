package com.esandi.healthmatecore.data

import kotlinx.coroutines.flow.Flow

class GoalsRepository(
    private val goalsDao: GoalsDao
) {

    fun getGoals(): Flow<Goals?> {
        return goalsDao.getGoals()
    }

    suspend fun saveGoals(goals: Goals) {
        goalsDao.saveGoals(goals)
    }
}