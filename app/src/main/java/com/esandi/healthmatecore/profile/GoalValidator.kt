package com.esandi.healthmatecore.profile

object GoalValidator {

    fun validateSteps(text: String): String? =
        validateWholeNumber(text, max = 100_000, maxMessage = "Keep it at 100,000 steps or less")

    fun validateCalories(text: String): String? =
        validateWholeNumber(text, max = 10_000, maxMessage = "Keep it at 10,000 kcal or less")

    fun validateWater(text: String): String? =
        validateWholeNumber(text, max = 10_000, maxMessage = "Keep it at 10,000 ml or less")

    fun validateSleepHours(text: String): String? {
        if (text.isBlank()) return "Enter a goal"

        val hours = text.toDoubleOrNull()
            ?: return "Use numbers only, like 7.5"

        if (hours <= 0) return "Goal must be more than 0"

        if (hours > 16) return "Keep it at 16 hours or less"

        return null
    }

    fun sleepHoursToMinutes(text: String): Int =
        ((text.toDoubleOrNull() ?: 0.0) * 60).toInt()

    private fun validateWholeNumber(
        text: String,
        max: Int,
        maxMessage: String
    ): String? {
        if (text.isBlank()) return "Enter a goal"

        val value = text.toIntOrNull()
            ?: return "Use whole numbers only"

        if (value <= 0) return "Goal must be more than 0"

        if (value > max) return maxMessage

        return null
    }
}