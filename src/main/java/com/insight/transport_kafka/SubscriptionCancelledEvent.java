package com.insight.transport_kafka;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class SubscriptionCancelledEvent extends KafkaEventWrapper {

    private String reason; // can be null

}
