package com.sammidev.utils

import lombok.extern.slf4j.Slf4j
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@Slf4j
class NIMGeneratorTest {
    lateinit var nimGenerator: NIMGenerator

    @BeforeEach
    internal fun setUp() {
        nimGenerator = NIMGenerator()
    }

    @Test
    internal fun generateTestSuccess() {
        val resultList = mutableSetOf<String>()

        for (i in 1..10) {
            val result = nimGenerator.generate()
            resultList.add(result)
        }

        assertEquals(resultList.size, 10)
        println(resultList)
    }
}