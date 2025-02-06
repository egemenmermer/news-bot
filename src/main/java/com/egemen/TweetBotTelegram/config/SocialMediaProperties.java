package com.egemen.TweetBotTelegram.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.social-media")
public class SocialMediaProperties {
    private int maxRetries;
    private int postDelay;
    // Add getters/setters
}