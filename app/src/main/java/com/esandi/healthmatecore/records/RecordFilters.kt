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

fun recordSummary(record: HealthRecord): String {
    val parts = buildList {
        if (record.steps > 0) {
            add("%,d steps".format(record.steps))
        }

        if (record.calories > 0) {
            add("%,d kcal".format(record.calories))
        }

        if (record.waterMl > 0) {
            add("%,d ml water".format(record.waterMl))
        }

        if (record.sleepMinutes > 0) {
            val hours = record.sleepMinutes / 60
            val minutes = record.sleepMinutes % 60
            add("${hours}h ${minutes}m sleep")
        }
    }

    val recordedText = when {
        parts.isNotEmpty() -> parts.joinToString(" · ")
        !record.note.isNullOrBlank() -> record.note
        else -> "No health data recorded"
    }

    return recordedText
}


