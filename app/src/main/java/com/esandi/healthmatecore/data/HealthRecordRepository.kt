package com.esandi.healthmatecore.data

import kotlinx.coroutines.flow.Flow

class HealthRecordRepository(
    private val healthRecordDao: HealthRecordDao
) {

    fun getAllRecords(): Flow<List<HealthRecord>> {
        return healthRecordDao.getAllRecords()
    }

    fun getRecordsByDate(date: String): Flow<List<HealthRecord>> {
        return healthRecordDao.getRecordsByDate(date)
    }

    suspend fun getRecordById(id: Long): HealthRecord? {
        return healthRecordDao.getRecordById(id)
    }

    suspend fun addRecord(record: HealthRecord): Long {
        return healthRecordDao.insert(record)
    }

    suspend fun updateRecord(record: HealthRecord) {
        healthRecordDao.update(record)
    }

    suspend fun deleteRecord(record: HealthRecord) {
        healthRecordDao.delete(record)
    }
    suspend fun addSampleDataIfEmpty() {
        if (healthRecordDao.countRecords() == 0) {
            for (record in sampleRecords()) {
                healthRecordDao.insert(record)
            }
        }
    }
    fun getTotalsForDate(date: String): Flow<DailyTotals> {
        return healthRecordDao.getTotalsForDate(date)
    }
}
