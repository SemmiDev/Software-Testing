package com.sammidev.student;

import com.sammidev.utils.EmailValidator;
import com.sammidev.utils.NIMGenerator;
import com.sammidev.utils.PhoneNumberValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

public class StudentRegistrationServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private PhoneNumberValidator phoneNumberValidator;

    @Mock
    EmailValidator emailValidator;

    @Captor
    private ArgumentCaptor<Student> studentArgumentCaptor;
    private StudentRegistrationService undertest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        undertest = new StudentRegistrationService(studentRepository, phoneNumberValidator, emailValidator);
    }

    @Test
    void itShouldSaveNewStudent() {
        var id = UUID.randomUUID();
        var phone = "+6280000011133";
        var nim = new NIMGenerator().generate();
        var email = "aaaaaaaa@gmail.com";
        var student = Student.builder()
                .id(id)
                .name("sammmmmmmm")
                .nim(nim)
                .email(email)
                .phone(phone)
                .build();

        // a request
        StudentRegistrationRequest request = StudentRegistrationRequest.builder().student(student).build();

        // no student with phone passed
        given(studentRepository.selectStudentByPhoneNumber(phone))
                .willReturn(Optional.empty());

        // no student with email passed
        given(studentRepository.selectStudentByEmail(email))
                .willReturn(Optional.empty());

        // validaton
        given(phoneNumberValidator.test(phone)).willReturn(true);
        given(emailValidator.test(email)).willReturn(true);

        // when
        undertest.registerNewStudent(request);

        // then
        then(studentRepository).should().save(studentArgumentCaptor.capture());
        var studentArgumentCaptorValue = studentArgumentCaptor.getValue();
        assertThat(studentArgumentCaptorValue).isEqualTo(student);
    }

    @Test
    void itShouldNotSaveNewStudentWhenPhoneNumberIsInvalid() {
        var id = UUID.randomUUID();
        var phone = "+628000001113213213123";
        var nim = new NIMGenerator().generate();
        var email = "aaaaaaaa@gmail.com";
        var student = Student.builder()
                .id(id)
                .name("sammmmmmmm")
                .nim(nim)
                .email(email)
                .phone(phone)
                .build();

        // a request
        var studentrequest = StudentRegistrationRequest.builder()
                .student(student)
                .build();

        // valid phone
        given(phoneNumberValidator.test(phone)).willReturn(false);
        given(emailValidator.test(email)).willReturn(true);

        // when
        assertThatThrownBy(() -> undertest.registerNewStudent(studentrequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Phone Number " + phone + " is not valid");

        // then
        then(studentRepository).shouldHaveNoInteractions();
    }


    @Test
    void itShouldNotSaveNewStudentWhenEmailIsInvalid() {
        var id = UUID.randomUUID();
        var phone = "+6280000011133";
        var nim = new NIMGenerator().generate();
        var email = "aaaaaaaa.com";
        var student = Student.builder()
                .id(id)
                .name("sammmmmmmm")
                .nim(nim)
                .email(email)
                .phone(phone)
                .build();

        // a request
        var studentrequest = StudentRegistrationRequest.builder()
                .student(student)
                .build();

        // valid phone
        given(phoneNumberValidator.test(phone)).willReturn(true);
        given(emailValidator.test(email)).willReturn(false);

        // when
        assertThatThrownBy(() -> undertest.registerNewStudent(studentrequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Email " + email + " is not valid");

        // then
        then(studentRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldSaveNewCustomerWhenIdIsNull() {
        var phone = "+6280000011133";
        var nim = new NIMGenerator().generate();
        var email = "aaaaaaaa.com";
        var student = Student.builder()
                .id(null)
                .name("sammmmmmmm")
                .nim(nim)
                .email(email)
                .phone(phone)
                .build();

        // ... a request
        // a request
        var studentrequest = StudentRegistrationRequest.builder()
                .student(student)
                .build();


        // ... No customer with phone number passed
        given(studentRepository.selectStudentByPhoneNumber(phone))
                .willReturn(Optional.empty());
        given(studentRepository.selectStudentByEmail(email))
                .willReturn(Optional.empty());

        //... Valid phone number
        given(phoneNumberValidator.test(phone)).willReturn(true);
        given(emailValidator.test(email)).willReturn(true);

        // When
        undertest.registerNewStudent(studentrequest);

        // Then
        then(studentRepository).should().save(studentArgumentCaptor.capture());
        var studentCaptorValue = studentArgumentCaptor.getValue();
        assertThat(studentCaptorValue).isEqualToIgnoringGivenFields(student, "id");
        assertThat(studentCaptorValue.getId()).isNotNull();
    }

    @Test
    void itShouldSaveNewCustomerWhenIdAndNIMIsNull() {
        var phone = "+6280000011133";
        var email = "aaaaaaaa.com";
        var student = Student.builder()
                .id(null)
                .name("sammmmmmmm")
                .nim(null)
                .email(email)
                .phone(phone)
                .build();

        // a request
        var studentrequest = StudentRegistrationRequest.builder()
                .student(student)
                .build();


        // ... No customer with phone number passed
        given(studentRepository.selectStudentByPhoneNumber(phone)).willReturn(Optional.empty());
        given(studentRepository.selectStudentByEmail(email)).willReturn(Optional.empty());

        //... Valid phone number
        given(phoneNumberValidator.test(phone)).willReturn(true);
        given(emailValidator.test(email)).willReturn(true);

        // When
        undertest.registerNewStudent(studentrequest);

        // Then
        then(studentRepository).should().save(studentArgumentCaptor.capture());
        var studentCaptorValue = studentArgumentCaptor.getValue();
        assertThat(studentCaptorValue).isEqualToIgnoringGivenFields(student, "id");
        assertThat(studentCaptorValue).isEqualToIgnoringGivenFields(student, "nim");
        assertThat(studentCaptorValue.getId()).isNotNull();
    }

    @Test
    void itShouldNotSaveStudentWhenStudentExists() {
        var id = UUID.randomUUID();
        var phone = "+6280000011133";
        var nim = new NIMGenerator().generate();
        var email = "aaaaaaaa.com";
        var student = Student.builder()
                .id(id)
                .name("sammmmmmmm")
                .nim(nim)
                .email(email)
                .phone(phone)
                .build();

        var studentrequest = StudentRegistrationRequest.builder()
                .student(student)
                .build();

        given(studentRepository.selectStudentByPhoneNumber(phone))
                .willReturn(Optional.of(student));

        //... Valid phone number
        given(phoneNumberValidator.test(phone)).willReturn(true);
        given(emailValidator.test(email)).willReturn(true);


        // When
        undertest.registerNewStudent(studentrequest);

        // Then
        then(studentRepository).should(never()).save(any());
    }


    @Test
    void itShouldThrowWhenPhoneNumberIsTaken() {
        var id = UUID.randomUUID();
        var phone = "+6280000011133";
        var nim = new NIMGenerator().generate();
        var email = "aaaaaaaa.com";
        var student = Student.builder()
                .id(id)
                .name("sammmmmmmm")
                .nim(nim)
                .email(email)
                .phone(phone)
                .build();

        var student2 = Student.builder()
                .id(UUID.randomUUID())
                .name("sammidev")
                .nim(new NIMGenerator().generate())
                .email("sam@gmail.co.id")
                .phone("+6280000011133")
                .build();

        var studentrequest = StudentRegistrationRequest.builder().student(student).build();

        // ... No student with phone number passed
        given(studentRepository.selectStudentByPhoneNumber(phone))
                .willReturn(Optional.of(student2));

        //... Valid phone number and email
        given(phoneNumberValidator.test(phone)).willReturn(true);
        given(emailValidator.test(email)).willReturn(true);


        // When
        // Then
        assertThatThrownBy(() -> undertest.registerNewStudent(studentrequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("phone number [%s] is taken", phone));

        // Finally
        then(studentRepository).should(never()).save(any(Student.class));
    }
}