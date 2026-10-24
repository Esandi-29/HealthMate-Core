package com.esandi.healthmatecore.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HealthRecordDaoTest {

    private lateinit var db: HealthMateDatabase
    private lateinit var dao: HealthRecordDao

    @Before
    fun setUp() {
        val context =
            ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(
            context,
            HealthMateDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        dao = db.healthRecordDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertRecord_canBeReadBack() = runBlocking {
        val id = dao.insert(
            HealthRecord(
                date = "2026-10-22",
                time = "10:00",
                steps = 5000,
                calories = 0,
                waterMl = 250,
                sleepMinutes = 0
            )
        )

        val saved = dao.getRecordById(id)

        assertEquals(5000, saved?.steps)
        assertEquals(250, saved?.waterMl)
    }

    @Test
    fun updateRecord_savesNewValues() = runBlocking {
        val id = dao.insert(
            HealthRecord(
                date = "2026-10-22",
                time = "10:00",
                steps = 5000,
                calories = 0,
                waterMl = 250,
                sleepMinutes = 0
            )
        )

        val original = dao.getRecordById(id)!!
        dao.update(original.copy(steps = 8000))

        val updated = dao.getRecordById(id)
        assertEquals(8000, updated?.steps)
    }

    @Test
    fun deleteRecord_removesIt() = runBlocking {
        val id = dao.insert(
            HealthRecord(
                date = "2026-10-22",
                time = "10:00",
                steps = 5000,
                calories = 0,
                waterMl = 250,
                sleepMinutes = 0
            )
        )

        val saved = dao.getRecordById(id)!!
        dao.delete(saved)

        assertNull(dao.getRecordById(id))
    }

    @Test
    fun getAllRecords_returnsNewestDateFirst() = runBlocking {
        dao.insert(
            HealthRecord(
                date = "2026-10-20",
                time = "10:00",
                steps = 2000,
                calories = 0,
                waterMl = 0,
                sleepMinutes = 0
            )
        )

        dao.insert(
            HealthRecord(
                date = "2026-10-22",
                time = "10:00",
                steps = 8000,
                calories = 0,
                waterMl = 0,
                sleepMinutes = 0
            )
        )

        val records = dao.getAllRecords().first()

        assertEquals("2026-10-22", records.first().date)
    }

    @Test
    fun getRecordsByDate_returnsOnlyMatchingDate() = runBlocking {
        dao.insert(
            HealthRecord(
                date = "2026-10-20",
                time = "10:00",
                steps = 2000,
                calories = 0,
                waterMl = 0,
                sleepMinutes = 0
            )
        )

        dao.insert(
            HealthRecord(
                date = "2026-10-22",
                time = "10:00",
                steps = 8000,
                calories = 0,
                waterMl = 0,
                sleepMinutes = 0
            )
        )

        val records =
            dao.getRecordsByDate("2026-10-22").first()

        assertEquals(1, records.size)
        assertEquals("2026-10-22", records.first().date)
    }

    @Test
    fun getTotalsForDate_sumsWaterRecords() = runBlocking {
        dao.insert(
            HealthRecord(
                date = "2026-10-22",
                time = "09:00",
                steps = 0,
                calories = 0,
                waterMl = 250,
                sleepMinutes = 0
            )
        )

        dao.insert(
            HealthRecord(
                date = "2026-10-22",
                time = "12:00",
                steps = 0,
                calories = 0,
                waterMl = 500,
                sleepMinutes = 0
            )
        )

        val totals =
            dao.getTotalsForDate("2026-10-22").first()

        assertEquals(750, totals.waterMl)
    }

    @Test
    fun getTotalsForEmptyDate_returnsZeroValues() = runBlocking {
        val totals =
            dao.getTotalsForDate("2026-10-25").first()

        assertEquals(0, totals.steps)
        assertEquals(0, totals.calories)
        assertEquals(0, totals.waterMl)
        assertEquals(0, totals.sleepMinutes)
    }
}
