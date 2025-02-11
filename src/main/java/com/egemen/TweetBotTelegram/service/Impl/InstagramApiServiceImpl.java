package com.egemen.TweetBotTelegram.service.Impl;

import com.egemen.TweetBotTelegram.service.InstagramApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class InstagramApiServiceImpl implements InstagramApiService {
    private static final Logger log = LoggerFactory.getLogger(InstagramApiServiceImpl.class);

    @Value("${instagram.access.token}")
    private String accessToken;

    @Value("${instagram.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public InstagramApiServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String uploadMedia(String imageUrl) {
        try {
            String url = apiUrl + "/media";
            
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("image_url", imageUrl);
            body.add("access_token", accessToken);
            body.add("media_type", "IMAGE");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            
            // Returns container ID
            return restTemplate.postForObject(url, request, String.class);
        } catch (Exception e) {
            log.error("Error uploading media to Instagram: {}", e.getMessage());
            throw new RuntimeException("Failed to upload media to Instagram", e);
        }
    }

    @Override
    public String publishMedia(String containerId, String caption) {
        try {
            String url = apiUrl + "/media/publish";
            
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("creation_id", containerId);
            body.add("access_token", accessToken);
            body.add("caption", caption);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            
            // Returns post ID
            return restTemplate.postForObject(url, request, String.class);
        } catch (Exception e) {
            log.error("Error publishing media to Instagram: {}", e.getMessage());
            throw new RuntimeException("Failed to publish media to Instagram", e);
        }
    }

    @Override
    public String getMediaStatus(String containerId) {
        try {
            String url = apiUrl + "/media/" + containerId;
            
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("access_token", accessToken);
            
            return restTemplate.getForObject(url + "?access_token={token}", 
                    String.class, accessToken);
        } catch (Exception e) {
            log.error("Error checking media status: {}", e.getMessage());
            throw new RuntimeException("Failed to check media status", e);
        }
    }
}