package com.egemen.TweetBotTelegram.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import jakarta.annotation.PostConstruct;

@Configuration
@PropertySources({
    @PropertySource(value = "file:.env", ignoreResourceNotFound = true)
})
public class EnvConfig {

    @PostConstruct
    public void loadEnvVariables() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

        System.setProperty("GEMINI_API_KEY", dotenv.get("GEMINI_API_KEY"));
        System.setProperty("HUGGINGFACE_API_KEY", dotenv.get("HUGGINGFACE_API_KEY"));
        System.setProperty("MEDIASTACK_API_KEY", dotenv.get("MEDIASTACK_API_KEY"));

        // Verify environment variables are loaded
        System.out.println("✅ Environment Variables Loaded:");
        System.out.println("DB_URL: " + System.getenv("DB_URL"));
        System.out.println("GEMINI_API_KEY: " + System.getenv("GEMINI_API_KEY"));
    }
}