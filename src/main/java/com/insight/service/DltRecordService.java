package com.insight.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insight.database.entity.DltRecord;
import com.insight.database.enums.DltStatus;
import com.insight.repository.DltRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DltRecordService {

    private final DltRecordRepository repository;
    private final ObjectMapper objectMapper;

    // even if an exception is thrown, do not mark the transaction
    //  as ‘rollback-only’
    @Transactional(noRollbackFor = Exception.class)
    public void save(String payload, String originalTopic, String exceptionMessage) {
        try {
            JsonNode node = objectMapper.readTree(payload);

            // Handle legacy double-encoded payloads (root is a JSON string, not an object)
            if (node.isTextual()) {
                node = objectMapper.readTree(node.asText());
            }

            UUID eventId = extractUuid(node, "eventId");
            Long companyId = extractLong(node, "companyId");

            // Idempotence
            if (eventId != null && repository.findByEventId(eventId).isPresent()) {
                log.warn("DLT record for eventId={} already exists, skipping", eventId);
                return;
            }

            DltRecord record = DltRecord.builder()
                    .eventId(eventId)
                    .companyId(companyId)
                    .originalTopic(originalTopic)
                    .exceptionMessage(extractCause(exceptionMessage))
                    .payload(payload)
                    .failedAt(LocalDateTime.now())
                    .status(DltStatus.PENDING)
                    .build();

            repository.save(record);
            log.info("DLT persisted: eventId={} companyId={} topic={}",
                    eventId, companyId, originalTopic);

        } catch (Exception e) {
            // CRITICAL: If we can't persist data to the database, we MUST NOT
            // throw an exception → otherwise the DLT consumer will retry indefinitely
            // We log an ERROR for alerting, and the message still remains in the DLT topic
            log.error("Failed to persist DLT record. Topic={} Payload={}",
                    originalTopic, payload, e);
        }
    }

    // Spring Kafka wraps exceptions: "Listener failed; <real cause>"
    // We extract the part after the last "; " to get the business exception message
    private String extractCause(String exceptionMessage) {
        if (exceptionMessage == null) return "unknown";
        int idx = exceptionMessage.lastIndexOf("; ");
        return idx >= 0 ? exceptionMessage.substring(idx + 2).trim() : exceptionMessage.trim();
    }

    private UUID extractUuid(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) return null;
        try {
            return UUID.fromString(value.asText());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Long extractLong(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return (value == null || value.isNull()) ? null : value.asLong();
    }
}