package com.start_up_insight_engine.transport_kafka;

import lombok.Getter;

@Getter
public class SubscriptionCancelledEvent extends KafkaEventWrapper {

    private String reason; // can be null

}
