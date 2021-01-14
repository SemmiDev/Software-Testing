package com.sammidev.payment;

import com.sammidev.student.Student;
import com.sammidev.student.StudentRepository;
import com.sammidev.uktPayment.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static com.sammidev.uktPayment.Currency.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

public class PaymentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CardPaymentCharger cardPaymentCharger;

    private PaymentService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new PaymentService(
                studentRepository,
                paymentRepository,
                cardPaymentCharger);
    }

    @Test
    void itShouldChargeCardSuccessfully() {
        // Given
        UUID studentId = UUID.randomUUID();

        // ... student exists
        given(studentRepository.findById(studentId)).willReturn(Optional.of(mock(Student.class)));

        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("100.00"),
                        IDR,
                        "card123xx",
                        "UKT"
                )
        );

        // ... Card is charged successfully
        given(cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(true));

        // When
        underTest.chargeCard(studentId, paymentRequest);

        // Then
        ArgumentCaptor<Payment> paymentArgumentCaptor =
                ArgumentCaptor.forClass(Payment.class);

        then(paymentRepository).should().save(paymentArgumentCaptor.capture());

        Payment paymentArgumentCaptorValue = paymentArgumentCaptor.getValue();
        assertThat(paymentArgumentCaptorValue).isEqualToIgnoringGivenFields(
                paymentRequest.getPayment(),"studentId");

        assertThat(paymentArgumentCaptorValue.getStudentID()).isEqualTo(studentId);
    }

    @Test
    void itShouldThrowWhenCardIsNotCharged() {
        // Given
        UUID studentId = UUID.randomUUID();

        // ... student exists
        given(studentRepository.findById(studentId)).willReturn(Optional.of(mock(Student.class)));

        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("100.00"),
                        IDR,
                        "card123xx",
                        "UKT"
                )
        );

        // ... Card is not charged successfully
        given(cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(false));

        // When
        // Then
        assertThatThrownBy(() -> underTest.chargeCard(studentId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Card not debited for student " + studentId);

        // ... No interaction with paymentRepository
        then(paymentRepository).shouldHaveNoInteractions();    }

    @Test
    void itShouldNotChargeCardAndThrowWhenCurrencyNotSupported() {
        // Given
        UUID studentId = UUID.randomUUID();

        // ... student exists
        given(studentRepository.findById(studentId)).willReturn(Optional.of(mock(Student.class)));

        Currency rm = RM;
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("100.00"),
                        rm,
                        "card123xx",
                        "UKT"
                )
        );

        // ... Card is charged successfully
        given(cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(true));


        // When
        assertThatThrownBy(() -> underTest.chargeCard(studentId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Currency [" + rm + "] not supported");

        // ... No interaction with cardPaymentCharger
        then(cardPaymentCharger).shouldHaveNoInteractions();

        // ... No interaction with paymentRepository
        then(paymentRepository).shouldHaveNoInteractions();
    }


    @Test
    void itShouldNotChargeAndThrowWhenCustomerNotFound() {
        // Given
        UUID studentId = UUID.randomUUID();

        // ... student exists
        given(studentRepository.findById(studentId)).willReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.chargeCard(studentId, new PaymentRequest(new Payment())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Student with id [" + studentId + "] not found");

        // ... No interactions with PaymentCharger not PaymentRepository
        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
    }
}