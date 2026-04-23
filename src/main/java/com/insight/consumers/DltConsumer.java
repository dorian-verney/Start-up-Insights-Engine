package com.insight.consumers;

import com.insight.service.DltRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DltConsumer {

    @Autowired
    private DltRecordService dltRecordService;

    @KafkaListener(topics = {
            "addon-events-dlt",
            "payment-events-dlt",
            "subscription-events-dlt"
    })
    public void handleDlt(
            String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String exceptionMessage,
            @Header(value = KafkaHeaders.ORIGINAL_TOPIC, required = false) String originalTopic
    ) {
        log.error("DATA LOST — all retries exhausted. originalTopic={} dltTopic={} cause=\"{}\" payload={}",
                originalTopic, topic, exceptionMessage, message);

        // Persist pour investigation / replay manuel
        String topicToStore = originalTopic != null ? originalTopic : topic;
        dltRecordService.save(message, topicToStore, exceptionMessage);
    }
}
