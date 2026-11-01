package com.esandi.healthmatecore.gamification

import com.esandi.healthmatecore.data.DayTotals
import com.esandi.healthmatecore.data.Goals
import java.time.LocalDate

data class Badge(
    val name: String,
    val earnedDate: LocalDate?,
    val lockedHint: String
)

object GamificationCalculator {

    fun currentStreak(
        history: List<DayTotals>,
        stepsGoal: Int,
        today: LocalDate
    ): Int {

        val goodDays = history
            .filter { it.steps >= stepsGoal }
            .map { LocalDate.parse(it.date) }
            .toSet()

        var checking = if (today in goodDays) {
            today
        } else {
            today.minusDays(1)
        }

        var count = 0

        while (checking in goodDays) {
            count++
            checking = checking.minusDays(1)
        }

        return count
    }
    fun badges(
        history: List<DayTotals>,
        goals: Goals
    ): List<Badge> {
        val sortedHistory = history.sortedBy { it.date }

        val first10k = sortedHistory.firstOrNull {
            it.steps >= 10_000
        }

        val sevenDayStreak = sortedHistory
            .windowed(7)
            .firstOrNull { days ->
                        days.zipWithNext().all { (first, second) ->
                            LocalDate.parse(second.date) ==
                                    LocalDate.parse(first.date).plusDays(1)
                        } &&
                        days.all { it.steps >= goals.steps }
            }

        val perfectDay = sortedHistory.firstOrNull {
            it.steps >= goals.steps &&
                    it.calories >= goals.calories &&
                    it.waterMl >= goals.waterMl &&
                    it.sleepMinutes >= goals.sleepMinutes
        }

        val totalSteps = sortedHistory.sumOf { it.steps }
        var runningSteps = 0
        var hundredKDate: LocalDate? = null

        for (day in sortedHistory) {
            runningSteps += day.steps

            if (runningSteps >= 100_000) {
                hundredKDate = LocalDate.parse(day.date)
                break
            }
        }

        return listOf(
            Badge(
                name = "First 10k",
                earnedDate = first10k?.let { LocalDate.parse(it.date) },
                lockedHint = "Walk 10,000 steps"
            ),
            Badge(
                name = "7-day streak",
                earnedDate = sevenDayStreak?.last()?.let {
                    LocalDate.parse(it.date)
                },
                lockedHint = "7 days in a row"
            ),
            Badge(
                name = "Perfect day",
                earnedDate = perfectDay?.let { LocalDate.parse(it.date) },
                lockedHint = "All 4 goals"
            ),
            Badge(
                name = "100k steps",
                earnedDate = hundredKDate,
                lockedHint = "${totalSteps / 1000}k so far"
            )
        )
    }
}