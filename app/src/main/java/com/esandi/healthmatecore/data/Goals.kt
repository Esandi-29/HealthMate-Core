package com.esandi.healthmatecore.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goals(
    @PrimaryKey
    val id: Int = 1,
    val steps: Int = 8000,
    val calories: Int = 400,
    @ColumnInfo(name = "water")
    val waterMl: Int = 2000,
    @ColumnInfo(name = "sleep_minutes")
    val sleepMinutes: Int = 480,
    @ColumnInfo(name = "active_days")
    val activeDays: Int = 5
)