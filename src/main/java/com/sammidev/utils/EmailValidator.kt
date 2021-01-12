package com.sammidev.utils

import org.springframework.stereotype.Component
import java.util.function.Predicate
import java.util.regex.Pattern

@Component
class EmailValidator : Predicate<String> {

    private val isEmailValid = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE
    ).asPredicate();

    override fun test(email: String): Boolean {
        return isEmailValid.test(email)
    }
}