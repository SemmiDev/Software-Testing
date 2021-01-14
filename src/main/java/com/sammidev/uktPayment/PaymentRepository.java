package com.sammidev.uktPayment;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
}
