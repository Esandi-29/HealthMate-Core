package com.esandi.healthmatecore.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthRecordDao {

    @Insert
    suspend fun insert(record: HealthRecord): Long

    @Update
    suspend fun update(record: HealthRecord)

    @Delete
    suspend fun delete(record: HealthRecord)

    @Query(
        "SELECT * FROM health_records " +
                "ORDER BY date DESC, time DESC"
    )
    fun getAllRecords(): Flow<List<HealthRecord>>

    @Query(
        "SELECT * FROM health_records " +
                "WHERE date = :date " +
                "ORDER BY time DESC"
    )
    fun getRecordsByDate(date: String): Flow<List<HealthRecord>>

    @Query(
        "SELECT * FROM health_records WHERE id = :id"
    )
    suspend fun getRecordById(id: Long): HealthRecord?

    @Query("SELECT COUNT(*) FROM health_records")
    suspend fun countRecords(): Int

    @Query(
        "SELECT " +
                "COALESCE(SUM(steps), 0) AS steps, " +
                "COALESCE(SUM(calories), 0) AS calories, " +
                "COALESCE(SUM(water), 0) AS waterMl, " +
                "COALESCE(SUM(sleep_minutes), 0) AS sleepMinutes " +
                "FROM health_records " +
                "WHERE date = :date"
    )
    fun getTotalsForDate(date: String): Flow<DailyTotals>
}
