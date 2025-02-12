package com.egemen.TweetBotTelegram.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class InstagramConfig {
    
    @Value("${instagram.access.token}")
    private String accessToken;

    @Bean("instagramRestTemplate")
    public RestTemplate instagramRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().setBearerAuth(accessToken);
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}