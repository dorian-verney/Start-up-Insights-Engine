package com.start_up_insight_engine.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.start_up_insight_engine.exceptions.EventProcessingException;
import com.start_up_insight_engine.service.AddOnService;
import com.start_up_insight_engine.transport_kafka.AddOnAddedEvent;
import com.start_up_insight_engine.transport_kafka.AddOnRemovedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// TODO design pattern strategy

@Service
@Slf4j
public class AddOnConsumer {

    @Autowired
    private AddOnService addonService;

    @Autowired
    private ObjectMapper objectMapper;

    @RetryableTopic(
            attempts = "4",
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            exclude = { JsonProcessingException.class, IllegalArgumentException.class }
    )
    @KafkaListener(topics = "addon-events")
    public void consume(String message,
                        @Header(value = KafkaHeaders.DELIVERY_ATTEMPT, required = false) Integer attempt
    ) throws EventProcessingException, JsonProcessingException {
        if (attempt != null && attempt > 1) {
            log.warn("Retry attempt {}/4 for addon event: {}", attempt, message);
        }
        JsonNode node = objectMapper.readTree(message);
        String eventType = node.get("eventType").asText();

        switch (eventType) {
            case "AddonAdded" -> {
                AddOnAddedEvent event = objectMapper.readValue(message, AddOnAddedEvent.class);
                addonService.handleAddonAdded(event);
            }
            case "AddonRemoved" -> {
                AddOnRemovedEvent event = objectMapper.readValue(message, AddOnRemovedEvent.class);
                addonService.handleAddonRemoved(event);
            }
            default -> {
                log.warn("Unknown addon event type: {}", eventType);
                throw new IllegalArgumentException("Unknown event type: " + eventType);
            }
        }
    }

}
