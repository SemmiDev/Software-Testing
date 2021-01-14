package com.sammidev.uktPayment;

import com.sammidev.student.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.sammidev.uktPayment.Currency.*;

@Service
public class PaymentService {
    private static final List<Currency> ACCEPTED_CURRENCIES = List.of(USD,IDR,EUR);

    private final StudentRepository studentRepository;
    private final PaymentRepository paymentRepository;
    private final CardPaymentCharger cardPaymentCharger;

    @Autowired
    public PaymentService(StudentRepository studentRepository,
                          PaymentRepository paymentRepository,
                          CardPaymentCharger cardPaymentCharger) {
        this.studentRepository = studentRepository;
        this.paymentRepository = paymentRepository;
        this.cardPaymentCharger = cardPaymentCharger;
    }

    public void chargeCard(UUID studentId, PaymentRequest paymentRequest) {
        boolean isStudentFound = studentRepository.findById(studentId).isPresent();
        if (!isStudentFound) {
            throw new IllegalStateException(String.format("Student with id [%s] not found", studentId));
        }

        boolean isCurrencySupported = ACCEPTED_CURRENCIES.contains(paymentRequest.getPayment().getCurrency());
        if (!isCurrencySupported) {
            String message = String.format(
                    "Currency [%s] not supported",
                    paymentRequest.getPayment().getCurrency());
            throw new IllegalStateException(message);
        }

        CardPaymentCharge cardPaymentCharge = cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        );

        if (!cardPaymentCharge.isCardDebited()) {
            throw new IllegalStateException(String.format("Card not debited for student %s", studentId));
        }

        paymentRequest.getPayment().setStudentID(studentId);
        paymentRepository.save(paymentRequest.getPayment());
    }
}