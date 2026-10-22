package com.esandi.healthmatecore.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_records")
data class HealthRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val date: String,
    val time: String,
    val steps: Int = 0,
    val calories: Int = 0,
    @ColumnInfo(name = "water")
    val waterMl: Int = 0,
    @ColumnInfo(name = "sleep_minutes")
    val sleepMinutes: Int = 0,
    val note: String? = null
)

fun sampleRecords(): List<HealthRecord> {
    return listOf(
        HealthRecord(
            id = 1,
            date = "2026-10-22",
            time = "08:30",
            steps = 3200
        ),
        HealthRecord(
            id = 2,
            date = "2026-10-22",
            time = "10:15",
            waterMl = 500,
            note = "Water after breakfast"
        ),
        HealthRecord(
            id = 3,
            date = "2026-10-21",
            time = "18:00",
            calories = 250
        ),
        HealthRecord(
            id = 4,
            date = "2026-10-21",
            time = "06:30",
            sleepMinutes = 450
        ),
        HealthRecord(
            id = 5,
            date = "2026-10-20",
            time = "07:45",
            steps = 1800
        )
    )
}
