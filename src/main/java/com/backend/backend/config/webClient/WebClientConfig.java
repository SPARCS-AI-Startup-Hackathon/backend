package com.backend.backend.config.webClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${spring.clova.api.url}")
    private String apiUrl;
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.TEXT_EVENT_STREAM))
                .build();
    }
}