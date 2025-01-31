package com.egemen.TweetBotTelegram.service.Impl;

import com.egemen.TweetBotTelegram.service.GeminiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GeminiServiceImpl implements GeminiService {

    @Value("${GEMINI_API_KEY}")
    private String geminiApiKey;

    @Override
    public String summarizeNews(String newsTitle, String newsContent) {
        String GEMINI_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + geminiApiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();
        content.put("text", "Summarize this news in one or two sentences: " + newsTitle + " - " + newsContent);
        contents.add(content);
        requestBody.put("contents", Collections.singletonList(Map.of("parts", contents)));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(GEMINI_ENDPOINT, entity, Map.class);

        if (response.getBody() != null && response.getBody().containsKey("candidates")) {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            if (!candidates.isEmpty()) {
                Map<String, Object> contentResponse = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) contentResponse.get("parts");
                return parts.get(0).get("text").toString();
            }
        }
        return "Error generating summary.";
    }

    @Override
    public String generateTweet(String newsSummary) {
        String GEMINI_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + geminiApiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();
        content.put("text", "Generate a short, engaging tweet (maximum 280 characters) about this news: " + newsSummary);
        contents.add(content);
        requestBody.put("contents", Collections.singletonList(Map.of("parts", contents)));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(GEMINI_ENDPOINT, entity, Map.class);

        if (response.getBody() != null && response.getBody().containsKey("candidates")) {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            if (!candidates.isEmpty()) {
                Map<String, Object> contentResponse = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) contentResponse.get("parts");
                return parts.get(0).get("text").toString();
            }
        }
        return "Error generating tweet.";
    }
}