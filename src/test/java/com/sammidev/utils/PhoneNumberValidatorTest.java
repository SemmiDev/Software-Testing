package com.sammidev.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class PhoneNumberValidatorTest {

    private PhoneNumberValidator undertest;

    @BeforeEach
    void setUp() {
        undertest = new PhoneNumberValidator();
    }

    @ParameterizedTest
    @CsvSource({
            "+6280000000033,true",
            "+6480000000033,false",
            "+6280000000023,true",
            "+62800000000sdfsf33,false",
            "+628000000o0033,false",
            "6280002a000001,false"
    })
    void itShouldValidatePhoneNummber(String phone, Boolean expected) {
        var isValid = undertest.test(phone);
        assertThat(isValid).isEqualTo(expected);
    }
}