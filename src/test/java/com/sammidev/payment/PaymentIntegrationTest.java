package com.sammidev.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sammidev.student.Student;
import com.sammidev.student.StudentRegistrationRequest;
import com.sammidev.uktPayment.Currency;
import com.sammidev.uktPayment.Payment;
import com.sammidev.uktPayment.PaymentRepository;
import com.sammidev.uktPayment.PaymentRequest;
import com.sammidev.utils.NIMGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import static com.sammidev.uktPayment.Currency.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void itShouldCreatePaymentSuccessfully() throws Exception{

        UUID studentId = UUID.randomUUID();
        var student = Student.builder()
                .id(studentId)
                .nim(new NIMGenerator().generate())
                .name("sammidev")
                .email("sammidev@gmail.com")
                .phone("+6280000011133")
                .build();

        var studentRequest = StudentRegistrationRequest.builder().student(student).build();

        // register student
        ResultActions studentRegResultActions = mockMvc.perform(put("/api/v1/student-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .contentType(Objects.requireNonNull(objectToJson(studentRequest))));

        // payment
        var paymentId = 1L;
        var payment =  Payment.builder()
                .paymentId(paymentId)
                .studentID(studentId)
                .amount(new BigDecimal(1000000))
                .currency(IDR)
                .source("atm bank mandiri")
                .description("PEMBAYARAN UKT")
                .build();

        PaymentRequest paymentRequest = new PaymentRequest(payment);
        // ... When payment is sent
        ResultActions paymentResultActions = mockMvc.perform(post("/api/v1/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(paymentRequest))));


        // Then both student registration and payment requests are 200 status code
        studentRegResultActions.andExpect(status().isOk());
        paymentResultActions.andExpect(status().isOk());

        // Payment is stored in db
        // TODO: Do not use paymentRepository instead create an endpoint to retrieve payments for customers
        assertThat(paymentRepository.findById(paymentId))
                .isPresent()
                .hasValueSatisfying(p -> assertThat(p).isEqualToComparingFieldByField(payment));

    }

    private String objectToJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            fail("Failed to convert object to json");
            return null;
        }
    }
}
