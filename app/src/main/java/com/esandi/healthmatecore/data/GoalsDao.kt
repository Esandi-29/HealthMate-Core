package com.esandi.healthmatecore.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalsDao {

    @Query("SELECT * FROM goals WHERE id = 1")
    fun getGoals(): Flow<Goals?>

    @Upsert
    suspend fun saveGoals(goals: Goals)
}