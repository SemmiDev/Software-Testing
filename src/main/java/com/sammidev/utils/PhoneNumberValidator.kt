package com.sammidev.utils

import org.springframework.stereotype.Component
import java.util.function.Predicate

@Component
class PhoneNumberValidator : Predicate<String> {

    override fun test(phoneNumber: String): Boolean = phoneNumber.startsWith("+62") && phoneNumber.length == 14
}