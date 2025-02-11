package com.egemen.TweetBotTelegram.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class EnvironmentValidator {
    private static final Logger log = LoggerFactory.getLogger(EnvironmentValidator.class);

    @Value("${aws.access.key:}")
    private String awsAccessKey;

    @Value("${aws.secret.key:}")
    private String awsSecretKey;

    @Value("${pexels.api.key:}")
    private String pexelsApiKey;

    @Value("${instagram.access.token:}")
    private String instagramToken;

    @Value("${mediastack.api.key:}")
    private String mediastackApiKey;

    @PostConstruct
    public void validateEnvironment() {
        log.info("Validating environment configuration...");
        
        boolean isValid = true;

        if (isEmpty(awsAccessKey) || isEmpty(awsSecretKey)) {
            log.error("❌ AWS credentials are not configured");
            isValid = false;
        }

        if (isEmpty(pexelsApiKey)) {
            log.error("❌ Pexels API key is not configured");
            isValid = false;
        }

        if (isEmpty(instagramToken)) {
            log.error("❌ Instagram access token is not configured");
            isValid = false;
        }

        if (isEmpty(mediastackApiKey)) {
            log.error("❌ Mediastack API key is not configured");
            isValid = false;
        }

        if (isValid) {
            log.info("✅ Environment validation successful");
        } else {
            log.warn("⚠️ Some environment variables are missing. Application may not function correctly.");
        }
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}