package com.sammidev.student;

import com.sammidev.utils.EmailValidator;
import com.sammidev.utils.NIMGenerator;
import com.sammidev.utils.PhoneNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class StudentRegistrationService {

    private final StudentRepository studentRepository;
    private final PhoneNumberValidator phoneNumberValidator;
    private final EmailValidator emailValidator;

    @Autowired
    public StudentRegistrationService(StudentRepository studentRepository,
                                      PhoneNumberValidator phoneNumberValidator,
                                      EmailValidator emailValidator){
        this.studentRepository = studentRepository;
        this.phoneNumberValidator = phoneNumberValidator;
        this.emailValidator = emailValidator;
    }

    public void registerNewStudent(StudentRegistrationRequest request) {

        String phoneNumber = request.getStudent().getPhone();
        String email = request.getStudent().getEmail();

        if (!phoneNumberValidator.test(phoneNumber)) {
            throw new IllegalStateException("Phone Number " + phoneNumber + " is not valid");
        }

        if (!emailValidator.test(email)) {
            throw new IllegalStateException("Email " + email + " is not valid");
        }

        Optional<Student> studentOptionalByPhone = studentRepository.selectCustomerByPhoneNumber(phoneNumber);
        Optional<Student> studentOptionalByEmail = studentRepository.selectCustomerByEmail(email);

        if (studentOptionalByPhone.isPresent()) {
            Student student = studentOptionalByPhone.get();
            if (student.getName().equals(request.getStudent().getName())) {
                return;
            }
            throw new IllegalStateException(String.format("phone number [%s] is taken", phoneNumber));
        }

        if (studentOptionalByEmail.isPresent()) {
            Student student = studentOptionalByEmail.get();
            if (student.getName().equals(request.getStudent().getName())) {
                return;
            }
            throw new IllegalStateException(String.format("Email [%s] is taken", phoneNumber));
        }

        // create ID and NIM
        if(request.getStudent().getId() == null) {
            request.getStudent().setId(UUID.randomUUID());
            request.getStudent().setNim(new NIMGenerator().generate());
        }

        studentRepository.save(request.getStudent());
    }
}
