package com.egemen.TweetBotTelegram.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Primary;

@Configuration
public class ImageConfig {

    @Bean("imageRestTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
}