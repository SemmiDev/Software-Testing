package com.sammidev.student;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentRegistrationRequest {

    private final Student student;

    public StudentRegistrationRequest(@JsonProperty("student") Student student) {
        this.student = student;
    }
}