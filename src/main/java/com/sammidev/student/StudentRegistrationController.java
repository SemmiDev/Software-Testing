package com.sammidev.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/student-registration")
public class StudentRegistrationController {

    private final StudentRegistrationService studentRegistrationService;

    @Autowired
    public StudentRegistrationController(StudentRegistrationService customerRegistrationService) {
        this.studentRegistrationService = customerRegistrationService;
    }

    @PutMapping
    public void registerNewStudent(@RequestBody StudentRegistrationRequest request) {
        studentRegistrationService.registerNewStudent(request);
    }
}
