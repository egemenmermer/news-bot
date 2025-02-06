package com.egemen.TweetBotTelegram.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    private final Map<String, Integer> requestCounts = new ConcurrentHashMap<>();
    private static final int RATE_LIMIT = 100; // requests per minute
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String clientIp = request.getRemoteAddr();
        int requests = requestCounts.getOrDefault(clientIp, 0);
        
        if (requests >= RATE_LIMIT) {
            response.setStatus(429); // Too Many Requests
            return false;
        }
        
        requestCounts.put(clientIp, requests + 1);
        return true;
    }
}