package com.esandi.healthmatecore.records

import com.esandi.healthmatecore.data.HealthRecord
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class DateRange(val label: String) {
    TODAY("Today"),
    WEEK("7 days"),
    MONTH("30 days"),
    ALL("All")
}

enum class MetricFilter(val label: String) {
    ALL("All"),
    STEPS("Steps"),
    CALORIES("Calories"),
    WATER("Water"),
    SLEEP("Sleep")
}

fun filterRecords(
    records: List<HealthRecord>,
    range: DateRange,
    metric: MetricFilter,
    today: LocalDate
): List<HealthRecord> {

    val earliest: LocalDate? = when (range) {
        DateRange.TODAY -> today
        DateRange.WEEK -> today.minusDays(6)
        DateRange.MONTH -> today.minusDays(29)
        DateRange.ALL -> null
    }

    return records.filter { record ->

        val recordDate = LocalDate.parse(record.date)

        val dateOk = earliest == null ||
                (!recordDate.isBefore(earliest) && !recordDate.isAfter(today))

        val metricOk = when (metric) {
            MetricFilter.ALL -> true
            MetricFilter.STEPS -> record.steps > 0
            MetricFilter.CALORIES -> record.calories > 0
            MetricFilter.WATER -> record.waterMl > 0
            MetricFilter.SLEEP -> record.sleepMinutes > 0
        }

        dateOk && metricOk
    }
}

fun dayLabel(date: String, today: LocalDate): String {
    val recordDate = LocalDate.parse(date)

    return when (recordDate) {
        today -> "Today"
        today.minusDays(1) -> "Yesterday"
        else -> recordDate.format(
            DateTimeFormatter.ofPattern("EEE d MMM")
        )
    }
}


