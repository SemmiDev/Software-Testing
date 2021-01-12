package com.sammidev.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class EmailValidatorTest {
    lateinit var undertest: EmailValidator

    @BeforeEach
    internal fun setUp() {
        undertest = EmailValidator()
    }

    @ParameterizedTest
    @CsvSource(*arrayOf(
            "sammidev@gmail.com,true",
            "sam@,false",
            "sammi,false",
            "assas@gmail,false",
            "assas@gmail.com,true",
            "assas@gmail.,false"
    ))
    fun itShouldEmailValid(email: String, expected: Boolean) {
        var isValid = undertest.test(email)
        assertThat(isValid).isEqualTo(expected)
    }
}