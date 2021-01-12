package com.sammidev.student;

import com.sammidev.utils.NIMGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        }
)
@Slf4j
public class StudentRepositoryTest {

    @Autowired
    private StudentRepository underTest;

    static Student student;

    @BeforeAll
    static void beforeAll() {
        var id = UUID.randomUUID();
        var phone = "+6280000011133";
        var nim = new NIMGenerator().generate();
        var email = "aaaaaaaa@gmail.com";
        student = Student.builder()
                .id(id)
                .name("sammmmmmmm")
                .nim(nim)
                .email(email)
                .phone(phone)
                .build();
    }

    @Test
    @DisplayName("isShouldSelectedStudentByPhoneNumberEmailAndNim")
    void isShouldSelectedStudent() {
        // Given
        var id = UUID.randomUUID();
        var phone = "+6280000000033";
        var nim = new NIMGenerator().generate();
        var email = "sammidev4@gmail.com";
        var student = Student.builder()
                .id(id)
                .name("sammidev")
                .nim(nim)
                .email(email)
                .phone(phone)
                .build();

        // when
        underTest.save(student);

        // then
        var optionalStudent1 = underTest.selectCustomerByPhoneNumber(phone);
        var optionalStudent2 = underTest.selectCustomerByNIM(nim);
        var optionalStudent3 = underTest.selectCustomerByEmail(email);

        assertThat(optionalStudent1).isPresent().hasValueSatisfying(a -> {
            assertThat(a).isEqualToComparingFieldByFieldRecursively(student); });
        assertThat(optionalStudent2).isPresent().hasValueSatisfying(a -> {
            assertThat(a).isEqualToComparingFieldByFieldRecursively(student); });
        assertThat(optionalStudent3).isPresent().hasValueSatisfying(a -> {
            assertThat(a).isEqualToComparingFieldByFieldRecursively(student); });

        log.info(String.valueOf(optionalStudent1));
        log.info(String.valueOf(optionalStudent2));
        log.info(String.valueOf(optionalStudent3));
    }

    @Test
    void itNotShouldSelectStudentByPhoneNumberWhenNumberDoesNotExists() {
        // Given
        var phoneNumber = "+123";

        // When
        var optionalStudent = underTest.selectCustomerByPhoneNumber(phoneNumber);

        // Then
        assertThat(optionalStudent).isNotPresent();
    }

    @Test
    void itNotShouldSelectStudentByNIMWhenNumberDoesNotExists() {
        // Given
        var nim = "123123123";

        // When
        var optionalStudent = underTest.selectCustomerByNIM(nim);

        // Then
        assertThat(optionalStudent).isNotPresent();
    }

    @Test
    void itNotShouldSelectStudentByEmailWhenNumberDoesNotExists() {
        // Given
        var email = "sammidev@gmail.com";

        // When
        var optionalStudent = underTest.selectCustomerByEmail(email);

        // Then
        assertThat(optionalStudent).isNotPresent();
    }

    @Test
    void itShouldSaveStudent() {
        // Given
        var id = UUID.randomUUID();
        var phone = "+6280000000033";
        var nim = new NIMGenerator().generate();
        var email = "sammidev4@gmail.com";
        var student = Student.builder()
                .id(id)
                .name("sammidev")
                .nim(nim)
                .email(email)
                .phone(phone)
                .build();

        // when
        underTest.save(student);

        // Then
        var optionalStudent = underTest.findById(id);
        assertThat(optionalStudent)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c).isEqualToComparingFieldByField(student);
                });
    }

    @Test
    void itShouldNotSaveStudentWhenNIMIsNull() {
        // Given
        var id = UUID.randomUUID();
        var phone = "+6280000000033";
        var email = "sammidev4@gmail.com";
        var student = Student.builder()
                .id(id)
                .name("sammidev")
                .nim(null)
                .email(email)
                .phone(phone)
                .build();

        // then
        assertThatThrownBy(() -> underTest.save(student))
                .hasMessageContaining("not-null property references a null or transient value : com.sammidev.student.Student.nim")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void itShouldNotSaveStudentWhenNIMIsLengthOver10() {
        // Given
        var id = UUID.randomUUID();
        var phone = "+6280000000033";
        var email = "sammidev4@gmail.com";
        var student = Student.builder()
                .id(id)
                .name("sammidev")
                .nim("13289473574389523431892472")
                .email(email)
                .phone(phone)
                .build();

        assertThatCode(() -> underTest.save(student));
    }

    @Test
    void itShouldNotSaveStudentWhenPhoneIsNull() {
        // Given
        var id = UUID.randomUUID();
        var nim = new NIMGenerator().generate();
        var email = "sammidev4@gmail.com";
        var student = Student.builder()
                .id(id)
                .name("sammidev")
                .nim(nim)
                .email(email)
                .phone(null)
                .build();

        // then
        assertThatThrownBy(() -> underTest.save(student))
                .hasMessageContaining("not-null property references a null or transient value : com.sammidev.student.Student.phone")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void itShouldSaveStudentWhenEmailIsNull() {
        // Given
        var id = UUID.randomUUID();
        var phone = "+6280000000033";
        var nim = new NIMGenerator().generate();
        var email = "sammidev4@gmail.com";
        var student = Student.builder()
                .id(id)
                .name("sammidev")
                .nim(nim)
                .email(null)
                .phone(phone)
                .build();

        // when
        underTest.save(student);

        // then
        var optionalStudent = underTest.findById(id);
        assertThat(optionalStudent)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c).isEqualToComparingFieldByField(student);
                });
    }

    @Test
    void deleteStudentSuccessfullEmail() {

        underTest.save(student);
        var select = underTest.selectCustomerByNIM(student.getNim());

        log.info(String.valueOf(select));

        underTest.deleteStudentByEmail("aaaaaaaa@gmail.com");
        var select2 = underTest.selectCustomerByNIM(student.getNim());
        assertThat(select2).isNotPresent();
    }

    @Test
    void updateNameOfStudentSuccessfullWithNIM() {

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

        underTest.save(student);
        underTest.updateStudentByNIM("sammmi nama baru", nim);
        var select2 = underTest.selectCustomerByNIM(nim);

        // then
        var optionalStudent = underTest.findById(id);
        assertThat(optionalStudent)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c.getName()).isEqualTo("sammmi nama baru");
                });

        log.info(String.valueOf(optionalStudent));
    }

    @Test
    void updateNamePhoneAndEmailOfStudentSuccessfullWithNIM() {

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

        underTest.save(student);

        underTest.updateNamePhoneEmailByNIM("UPDATE","+6280000011134","UPDATE@gmail.com", nim);
        var select2 = underTest.selectCustomerByNIM(nim);

        // then
        var optionalStudent = underTest.findById(id);
        assertThat(optionalStudent)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c.getName()).isEqualTo("UPDATE");
                    assertThat(c.getEmail()).isEqualTo("UPDATE@gmail.com");
                    assertThat(c.getPhone()).isEqualTo("+6280000011134");
                });

        log.info(String.valueOf(optionalStudent));
    }
}