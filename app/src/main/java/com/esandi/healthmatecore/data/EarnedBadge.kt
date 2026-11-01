package com.esandi.healthmatecore.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "earned_badges")
data class EarnedBadge(
    @PrimaryKey
    val name: String,

    @ColumnInfo(name = "earned_date")
    val earnedDate: String
)