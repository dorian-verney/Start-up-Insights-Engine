package com.insight.poller.client;

import com.insight.poller.client.dto.PollResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class CrmApiClient {

    private final RestClient crmRestClient;

    public PollResponseDto fetchSubscriptions(Instant updatedAfter, String cursor, int limit) {

        // construct URI
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/api/v1/subscriptions")
                .queryParam("limit", limit);

        if (updatedAfter != null) {
            uriBuilder.queryParam("updated_after", updatedAfter.toString());
        }
        if (cursor != null) {
            uriBuilder.queryParam("cursor", cursor);
        }

        String uri = uriBuilder.build().toUriString();
        log.info("Calling Mock API: {}", uri);

        return crmRestClient.get()
                .uri(uri)
                .retrieve()
                .body(PollResponseDto.class);
    }
}