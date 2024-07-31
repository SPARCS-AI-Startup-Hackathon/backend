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

    @Value("${spring.stt.api.stt_url}")
    private String sttUrl;
    @Bean
    public WebClient clovaWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.TEXT_EVENT_STREAM))
                .build();
    }
    @Bean
    public WebClient sttWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(sttUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.TEXT_EVENT_STREAM))
                .build();
    }
}