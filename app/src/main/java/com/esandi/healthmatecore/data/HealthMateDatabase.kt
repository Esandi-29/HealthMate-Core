package com.esandi.healthmatecore.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [HealthRecord::class, Goals::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
abstract class HealthMateDatabase : RoomDatabase() {

    abstract fun healthRecordDao(): HealthRecordDao

    abstract fun goalsDao(): GoalsDao
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