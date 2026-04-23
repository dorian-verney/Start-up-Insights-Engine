package com.insight.transport_kafka;

import com.insight.database.enums.AddOnType;
import com.insight.database.enums.PaymentType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.Month;

@Getter
public class PaymentSucceededEvent extends KafkaEventWrapper {

    @NotNull
    private PaymentType paymentType;

    private AddOnType addOnType;  // nullable

    @NotNull
    private Month month;

    @NotNull
    private Integer amount;
}
