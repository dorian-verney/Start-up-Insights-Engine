package com.insight.transport_kafka;

import lombok.Getter;

@Getter
public class SubscriptionCancelledEvent extends KafkaEventWrapper {

    private String reason; // can be null

}
