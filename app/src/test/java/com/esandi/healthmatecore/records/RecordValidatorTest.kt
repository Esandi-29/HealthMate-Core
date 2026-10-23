package com.esandi.healthmatecore.records

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RecordValidatorTest {

    @Test
    fun emptySteps_isValid() {
        assertNull(RecordValidator.validateSteps(""))
    }

    @Test
    fun normalSteps_isValid() {
        assertNull(RecordValidator.validateSteps("5000"))
    }

    @Test
    fun lettersInSteps_areRejected() {
        assertEquals(
            "Use numbers only",
            RecordValidator.validateSteps("abc")
        )
    }

    @Test
    fun negativeSteps_areRejected() {
        assertEquals(
            "Use numbers only",
            RecordValidator.validateSteps("-5")
        )
    }

    @Test
    fun stepsAtLimit_isValid() {
        assertNull(RecordValidator.validateSteps("100000"))
    }

    @Test
    fun stepsAboveLimit_returnsError() {
        assertEquals(
            "That's more than 100,000 steps",
            RecordValidator.validateSteps("100001")
        )
    }

    @Test
    fun waterAboveLimit_returnsError() {
        assertEquals(
            "That's more than 10,000 ml",
            RecordValidator.validateWater("10001")
        )
    }

    @Test
    fun sleepAtLimit_isValid() {
        assertNull(RecordValidator.validateSleep("960"))
    }

    @Test
    fun sleepAboveLimit_returnsError() {
        assertEquals(
            "That's more than 16 hours of sleep",
            RecordValidator.validateSleep("961")
        )
    }

    @Test
    fun allZeroValues_returnsError() {
        assertEquals(
            "Enter at least one value",
            RecordValidator.validateAtLeastOne(0, 0, 0, 0)
        )
    }

    @Test
    fun waterWithOtherValuesZero_isValid() {
        assertNull(
            RecordValidator.validateAtLeastOne(0, 0, 250, 0)
        )
    }
}
