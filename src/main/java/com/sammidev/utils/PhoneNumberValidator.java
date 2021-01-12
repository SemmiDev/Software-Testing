package com.sammidev.utils;

import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class PhoneNumberValidator implements Predicate<String> {

    @Override
    public boolean test(String phoneNumber) {
        return phoneNumber.startsWith("+62") && phoneNumber.length() == 14;
    }
}
