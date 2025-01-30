package com.egemen.TweetBotTelegram.service.Impl;

import com.egemen.TweetBotTelegram.service.GeminiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;
import java.util.HashMap;
import java.util.Map;

public class GeminiServiceImpl implements GeminiService {

    @Value("${GEMINI_API_KEY}")
    private String GEMINI_API_KEY;

    private final String GEMINI_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + GEMINI_API_KEY;

    @Override
    public String summarizeNews(String newsTitle, String newsContent) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("prompt", "Summarize this news in one or two sentences: " + newsTitle + " - " + newsContent);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(GEMINI_ENDPOINT, entity, Map.class);

        if (response.getBody() != null && response.getBody().containsKey("candidates")) {
            return response.getBody().get("candidates").toString();
        }
        return "Error generating summary.";
    }

    @Override
    public String generateTweet(String newsSummary) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("prompt", "Generate a short, engaging tweet (maximum 280 characters) about this news: " + newsSummary);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(GEMINI_ENDPOINT, entity, Map.class);

        if (response.getBody() != null && response.getBody().containsKey("candidates")) {
            return response.getBody().get("candidates").toString();
        }
        return "Error generating tweet.";
    }
}
