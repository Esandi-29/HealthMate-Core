package com.esandi.healthmatecore.gamification

import com.esandi.healthmatecore.data.DayTotals
import com.esandi.healthmatecore.data.Goals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class GamificationCalculatorTest {

    private val today = LocalDate.of(2026, 11, 1)

    @Test
    fun emptyHistory_returnsZeroStreak() {
        val streak = GamificationCalculator.currentStreak(
            history = emptyList(),
            stepsGoal = 8000,
            today = today
        )

        assertEquals(0, streak)
    }

    @Test
    fun missedDayBreaksStreak_returnsTwo() {
        val history = listOf(
            DayTotals(date = "2026-10-28", steps = 9000),
            DayTotals(date = "2026-10-29", steps = 5000),
            DayTotals(date = "2026-10-30", steps = 9000),
            DayTotals(date = "2026-10-31", steps = 8500),
            DayTotals(date = "2026-11-01", steps = 2000)
        )

        val streak = GamificationCalculator.currentStreak(
            history = history,
            stepsGoal = 8000,
            today = today
        )

        assertEquals(2, streak)
    }

    @Test
    fun threeGoodDaysIncludingToday_returnsStreakOfThree() {
        val history = listOf(
            DayTotals(date = "2026-10-30", steps = 9000),
            DayTotals(date = "2026-10-31", steps = 8500),
            DayTotals(date = "2026-11-01", steps = 10000)
        )

        val streak = GamificationCalculator.currentStreak(
            history = history,
            stepsGoal = 8000,
            today = today
        )

        assertEquals(3, streak)
    }

    @Test
    fun tenThousandStepsEarnsFirst10kBadge() {
        val history = listOf(
            DayTotals(date = "2026-10-25", steps = 10500)
        )

        val badges = GamificationCalculator.badges(
            history = history,
            goals = Goals(steps = 8000)
        )

        assertEquals(
            LocalDate.of(2026, 10, 25),
            badges[0].earnedDate
        )
    }

    @Test
    fun sevenConsecutiveGoodDaysEarnsBadgeOnSeventhDay() {
        val history = (1..7).map { day ->
            DayTotals(
                date = "2026-10-${day.toString().padStart(2, '0')}",
                steps = 9000
            )
        }

        val badges = GamificationCalculator.badges(
            history = history,
            goals = Goals(steps = 8000)
        )

        assertEquals(
            LocalDate.of(2026, 10, 7),
            badges[1].earnedDate
        )
    }

    @Test
    fun gapInSevenGoodDaysKeepsStreakBadgeLocked() {
        val history = listOf(
            DayTotals(date = "2026-10-01", steps = 9000),
            DayTotals(date = "2026-10-02", steps = 9000),
            DayTotals(date = "2026-10-03", steps = 9000),
            DayTotals(date = "2026-10-04", steps = 9000),
            DayTotals(date = "2026-10-05", steps = 9000),
            DayTotals(date = "2026-10-06", steps = 9000),
            DayTotals(date = "2026-10-08", steps = 9000)
        )

        val badges = GamificationCalculator.badges(
            history = history,
            goals = Goals(steps = 8000)
        )

        assertNull(badges[1].earnedDate)
    }
}
