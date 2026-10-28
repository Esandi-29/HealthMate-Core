package com.esandi.healthmatecore.data

data class DayTotals(
    val date: String,
    val steps: Int = 0,
    val calories: Int = 0,
    val waterMl: Int = 0,
    val sleepMinutes: Int = 0
)