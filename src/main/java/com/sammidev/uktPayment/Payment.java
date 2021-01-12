package com.sammidev.uktPayment;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Payment {

    @Id @GeneratedValue
    private Long paymentId;

    private UUID studentID;
    private BigDecimal amount;
    private Currency currency;
    private String source;
    private String description;
}
