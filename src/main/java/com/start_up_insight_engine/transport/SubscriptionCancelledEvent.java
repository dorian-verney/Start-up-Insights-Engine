package com.start_up_insight_engine.transport;

import jakarta.validation.constraints.NotNull;

public class SubscriptionCancelledEvent extends KafkaEventWrapper {

    private String reason; // can be null

}
