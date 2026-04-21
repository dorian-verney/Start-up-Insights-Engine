package com.start_up_insight_engine.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.start_up_insight_engine.exceptions.EventProcessingException;
import org.springframework.kafka.annotation.RetryableTopic;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.start_up_insight_engine.service.PaymentService;
import com.start_up_insight_engine.transport_kafka.OneTimePaymentEvent;
import com.start_up_insight_engine.transport_kafka.PaymentFailedEvent;
import com.start_up_insight_engine.transport_kafka.PaymentSucceededEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

// TODO design pattern strategy

@Service
@Slf4j
public class PaymentConsumer {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @RetryableTopic(
            attempts = "4",
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            exclude = { JsonProcessingException.class, IllegalArgumentException.class }
    )
    @KafkaListener(topics = "payment-events")
    public void consume(String message,
                        @Header(value = KafkaHeaders.DELIVERY_ATTEMPT, required = false) Integer attempt
    ) throws EventProcessingException, JsonProcessingException {
        if (attempt != null && attempt > 1) {
            log.warn("Retry attempt {}/4 for payment event: {}", attempt, message);
        }
        JsonNode node = objectMapper.readTree(message);
        String eventType = node.get("eventType").asText();

        switch (eventType) {
            case "PaymentSucceeded" -> {
                PaymentSucceededEvent event = objectMapper.readValue(message, PaymentSucceededEvent.class);
                paymentService.handlePaymentSucceeded(event);
            }
            case "PaymentFailed" -> {
                PaymentFailedEvent event = objectMapper.readValue(message, PaymentFailedEvent.class);
                paymentService.handlePaymentFailed(event);
            }
            case "OneTimePayment" -> {
                OneTimePaymentEvent event = objectMapper.readValue(message, OneTimePaymentEvent.class);
                paymentService.handleOneTimePayment(event);
            }
            default -> {
                log.warn("Unknown payment event type: {}", eventType);
                throw new IllegalArgumentException("Unknown event type: " + eventType);
            }
        }
    }
}