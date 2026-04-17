package com.start_up_insight_engine.consumers;

import com.start_up_insight_engine.service.AddOnService;
import com.start_up_insight_engine.transport_kafka.AddOnAddedEvent;
import com.start_up_insight_engine.transport_kafka.AddOnRemovedEvent;
import com.start_up_insight_engine.transport_kafka.KafkaEventWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class AddOnConsumer {

    @Autowired
    private AddOnService addonService;

    @Autowired
    private ObjectMapper objectMapper;


    @KafkaListener(topics = "addon-events")
    public void consume(String message){
        try {
            KafkaEventWrapper wrapper = objectMapper.readValue(message, KafkaEventWrapper.class);

            switch (wrapper.getEventType()) {
                case "AddonAdded" -> {
                    AddOnAddedEvent event = objectMapper.readValue(message, AddOnAddedEvent.class);
                    addonService.handleAddonAdded(event);
                }
                case "AddonRemoved" -> {
                    AddOnRemovedEvent event = objectMapper.readValue(message, AddOnRemovedEvent.class);
                    addonService.handleAddonRemoved(event);
                }
            }
        } catch (Exception e) {
            log.error("Failed to process addon event: {}", e.getMessage(), e);
        }
    }


}
