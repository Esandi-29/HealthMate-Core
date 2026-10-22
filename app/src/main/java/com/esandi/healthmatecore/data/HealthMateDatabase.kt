package com.esandi.healthmatecore.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [HealthRecord::class],
    version = 1,
    exportSchema = true
)
abstract class HealthMateDatabase : RoomDatabase() {

    abstract fun healthRecordDao(): HealthRecordDao

    companion object {
        @Volatile
        private var INSTANCE: HealthMateDatabase? = null

        fun getDatabase(context: Context): HealthMateDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    HealthMateDatabase::class.java,
                    "healthmate.db"
                )
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}