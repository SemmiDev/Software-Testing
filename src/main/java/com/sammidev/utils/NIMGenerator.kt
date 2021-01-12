package com.sammidev.utils

import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

class NIMGenerator {
    fun generate(): String {
        var nimTEMP = mutableListOf<Int>()
        var random = java.util.Random()

        for (i in 0..9) {
            var n = random.nextInt(10)
            nimTEMP.add(n)
        }

        var result1 = nimTEMP.toString().replace("[","")
        result1 = result1.replace("]","")
        result1 = result1.replace(",","")
        result1 = result1.replace(" ","")
        return result1
    }
}