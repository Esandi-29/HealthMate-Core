package com.esandi.healthmatecore.records

object RecordValidator {

    fun validateSteps(text: String): String? {
        return validateNumber(
            text = text,
            max = 100_000,
            maxMessage = "That's more than 100,000 steps"
        )
    }

    fun validateCalories(text: String): String? {
        return validateNumber(
            text = text,
            max = 10_000,
            maxMessage = "That's more than 10,000 kcal"
        )
    }

    fun validateWater(text: String): String? {
        return validateNumber(
            text = text,
            max = 10_000,
            maxMessage = "That's more than 10,000 ml"
        )
    }

    fun validateSleep(text: String): String? {
        return validateNumber(
            text = text,
            max = 960,
            maxMessage = "That's more than 16 hours of sleep"
        )
    }

    private fun validateNumber(
        text: String,
        max: Int,
        maxMessage: String
    ): String? {
        if (text.isBlank()) return null

        val value = text.toIntOrNull()
            ?: return "Use numbers only"

        if (value < 0) return "Use numbers only"

        if (value > max) return maxMessage

        return null
    }

    fun validateAtLeastOne(
        steps: Int,
        calories: Int,
        water: Int,
        sleep: Int
    ): String? {
        return if (
            steps == 0 &&
            calories == 0 &&
            water == 0 &&
            sleep == 0
        ) {
            "Enter at least one value"
        } else {
            null
        }
    }
}
