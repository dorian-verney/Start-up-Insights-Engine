package com.insight.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient crmRestClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:9090")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}