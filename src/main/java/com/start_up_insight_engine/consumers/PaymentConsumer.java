package com.start_up_insight_engine.consumers;

import tools.jackson.databind.ObjectMapper;
import com.start_up_insight_engine.service.PaymentService;
import com.start_up_insight_engine.transport_kafka.KafkaEventWrapper;
import com.start_up_insight_engine.transport_kafka.OneTimePaymentEvent;
import com.start_up_insight_engine.transport_kafka.PaymentFailedEvent;
import com.start_up_insight_engine.transport_kafka.PaymentSucceededEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

// TODO design pattern strategy

@Service
@Slf4j
public class PaymentConsumer {

    @Autowired
    private PaymentService addonService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "payment-events")
    public void consume(String message) {
        try {
            KafkaEventWrapper wrapper = objectMapper.readValue(message, KafkaEventWrapper.class);

            switch (wrapper.getEventType()) {
                case "PaymentSucceeded" -> {
                    PaymentSucceededEvent event = objectMapper.readValue(message, PaymentSucceededEvent.class);
                    addonService.handlePaymentSucceeded(event);
                }
                case "PaymentFailed" -> {
                    PaymentFailedEvent event = objectMapper.readValue(message, PaymentFailedEvent.class);
                    addonService.handlePaymentFailed(event);
                }
                case "OneTimePayment" -> {
                    OneTimePaymentEvent event = objectMapper.readValue(message, OneTimePaymentEvent.class);
                    addonService.handleOneTimePayment(event);
                }
            }
        } catch (Exception e) {
            log.error("Failed to process payment event: {}", e.getMessage(), e);
        }
    }
}