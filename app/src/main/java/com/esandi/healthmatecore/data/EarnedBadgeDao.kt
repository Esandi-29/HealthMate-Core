package com.esandi.healthmatecore.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EarnedBadgeDao {

    @Query("SELECT * FROM earned_badges")
    fun getAllEarnedBadges(): Flow<List<EarnedBadge>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(earnedBadge: EarnedBadge)
}