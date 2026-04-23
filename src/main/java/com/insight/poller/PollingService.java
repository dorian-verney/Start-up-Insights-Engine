package com.insight.poller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insight.poller.client.CrmApiClient;
import com.insight.poller.client.dto.PollResponseDto;
import com.insight.poller.translator.SubscriptionEventTranslator;
import com.insight.transport_kafka.SubscriptionStartedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PollingService {

    private final CrmApiClient crmApiClient;
    private final SubscriptionEventTranslator translator;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 5_000)  // toutes les 30 secondes
    public void pollCrm() {
        log.info("=== Starting CRM poll ===");

        // Pour commencer simple : on demande TOUTES les subs modifiées dans les 7 derniers jours
        // Plus tard (Phase 2), on aura un VRAI checkpoint persistant par tenant
        Instant updatedAfter = Instant.now().minus(7, ChronoUnit.DAYS);

        String cursor = null;
        int totalFetched = 0;
        int totalPages = 0;
        boolean hasMore = true;

        // Pagination loop
        do {
            PollResponseDto response = crmApiClient.fetchSubscriptions(updatedAfter, cursor, 50);

            if (response == null || response.getResults() == null) {
                log.warn("Empty response from CRM");
                break;
            }

            // Pour each sub, translate event then publishing
            response.getResults().forEach(sub -> {
                try {
                    SubscriptionStartedEvent event = translator.toSubscriptionStarted(sub);
                    String json = objectMapper.writeValueAsString(event);
                    kafkaTemplate.send("subscription-events", event.getCompanyId().toString(), json);
                    log.debug("Published event for sub {}", sub.getId());
                } catch (JsonProcessingException e) {
                    log.error("Failed to serialize event for sub {}", sub.getId(), e);
                }
            });

            totalFetched += response.getResults().size();
            totalPages++;
            cursor = response.getNextCursor();
            hasMore = response.isHasMore();

        } while (hasMore);

        log.info("=== Poll complete: {} subs across {} pages ===", totalFetched, totalPages);
    }
}