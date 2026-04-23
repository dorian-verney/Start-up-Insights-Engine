package com.insight.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.insight.exceptions.EventProcessingException;
import org.springframework.kafka.annotation.RetryableTopic;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insight.service.SubscriptionService;
import com.insight.transport_kafka.PlanChangedEvent;
import com.insight.transport_kafka.SubscriptionCancelledEvent;
import com.insight.transport_kafka.SubscriptionStartedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

// TODO system de ack

@Service
@Slf4j
public class SubscriptionConsumer {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private ObjectMapper objectMapper;

    @RetryableTopic(
            attempts = "4",
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            exclude = { JsonProcessingException.class, IllegalArgumentException.class }
    )
    @KafkaListener(topics = "subscription-events")
    public void consume(String message,
                        @Header(KafkaHeaders.RECEIVED_TOPIC) String receivedTopic
    ) throws EventProcessingException, JsonProcessingException {
        // Spring automatically creates a listen with -retry that this function can consume
        if (receivedTopic.contains("-retry")) {
            log.warn("Retry for subscription event on topic {}: {}", receivedTopic, message);
        }
        JsonNode node = objectMapper.readTree(message);
        String eventType = node.get("eventType").asText();

        switch (eventType) {
            case "SubscriptionStarted" -> {
                SubscriptionStartedEvent event = objectMapper.readValue(message, SubscriptionStartedEvent.class);
                subscriptionService.handleSubscriptionStarted(event);
            }
            case "SubscriptionCancelled" -> {
                SubscriptionCancelledEvent event = objectMapper.readValue(message, SubscriptionCancelledEvent.class);
                subscriptionService.handleSubscriptionCancelled(event);
            }
            case "PlanChanged" -> {
                PlanChangedEvent event = objectMapper.readValue(message, PlanChangedEvent.class);
                subscriptionService.handlePlanChanged(event);
            }
            default -> {
                log.warn("Unknown subscription event type: {}", eventType);
                throw new IllegalArgumentException("Unknown event type: " + eventType);
            }
        }
    }
}
