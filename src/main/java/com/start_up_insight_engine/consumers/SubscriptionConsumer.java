package com.start_up_insight_engine.consumers;

import tools.jackson.databind.ObjectMapper;
import com.start_up_insight_engine.service.SubscriptionService;
import com.start_up_insight_engine.transport_kafka.KafkaEventWrapper;
import com.start_up_insight_engine.transport_kafka.PlanChangedEvent;
import com.start_up_insight_engine.transport_kafka.SubscriptionCancelledEvent;
import com.start_up_insight_engine.transport_kafka.SubscriptionStartedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

// TODO design pattern strategy
// TODO double serialization /!\
// TODO system de ack

@Service
@Slf4j
public class SubscriptionConsumer {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private ObjectMapper objectMapper;


    @KafkaListener(topics = "subscription-events")
    public void consume(String message){
        try {
            KafkaEventWrapper wrapper = objectMapper.readValue(message, KafkaEventWrapper.class);

            switch (wrapper.getEventType()) {
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
            }
        } catch (Exception e) {
            log.error("Failed to process subscription event: {}", e.getMessage(), e);
        }
    }
}
