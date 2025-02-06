package com.egemen.TweetBotTelegram.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final AppConfig appConfig;

    public RateLimitInterceptor(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        Bucket bucket = buckets.computeIfAbsent(apiKey, this::createNewBucket);
        if (bucket.tryConsume(1)) {
            return true;
        }

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        return false;
    }

    private Bucket createNewBucket(String apiKey) {
        return Bucket.builder()
            .addLimit(Bandwidth.classic(appConfig.getSecurity().getRateLimit().getRequests(),
                Refill.intervally(appConfig.getSecurity().getRateLimit().getRequests(),
                    Duration.ofMinutes(appConfig.getSecurity().getRateLimit().getPerMinutes()))))
            .build();
    }
}