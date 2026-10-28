package com.esandi.healthmatecore.records

import java.time.Duration
import java.time.LocalTime

fun sleepMinutesBetween(
    bed: LocalTime,
    wake: LocalTime
): Int {
    val minutes = Duration.between(bed, wake).toMinutes().toInt()

    return if (minutes < 0) {
        minutes + 24 * 60
    } else {
        minutes
    }
}