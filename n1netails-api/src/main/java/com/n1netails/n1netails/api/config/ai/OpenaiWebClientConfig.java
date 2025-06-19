package com.n1netails.n1netails.api.config.ai;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
@Configuration
public class OpenaiWebClientConfig {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Bean
    public WebClient openaiWebClient() {
        return WebClient.builder()
                .baseUrl(this.getApiUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.getApiKey())
                .codecs(config -> config.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // Example 16MB buffer size
                .build();
    }
}
