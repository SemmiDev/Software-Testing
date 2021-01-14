package com.sammidev.payment;

import com.sammidev.uktPayment.Currency;
import com.sammidev.uktPayment.Payment;
import com.sammidev.uktPayment.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.UUID;

import static com.sammidev.uktPayment.Currency.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        }
)
public class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository underTest;

    @Test
    void itShouldInsertPayment() {
        Long paymentID = 1L;
        var payment = Payment.builder()
                .paymentId(null)
                .studentID(UUID.randomUUID())
                .amount(new BigDecimal(3000000))
                .currency(IDR)
                .description("BAYAR UKT")
                .build();

        underTest.save(payment);
        var paymentOptional = underTest.findById(paymentID);
        assertThat(paymentOptional)
                .isPresent()
                .hasValueSatisfying(p -> assertThat(p).isEqualTo(payment));
    }
}