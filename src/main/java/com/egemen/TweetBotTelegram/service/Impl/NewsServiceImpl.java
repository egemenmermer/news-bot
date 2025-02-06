package com.egemen.TweetBotTelegram.service.Impl;

import com.egemen.TweetBotTelegram.dto.NewsResponseDTO;
import com.egemen.TweetBotTelegram.entity.Bot;
import com.egemen.TweetBotTelegram.entity.News;
import com.egemen.TweetBotTelegram.repository.BotRepository;
import com.egemen.TweetBotTelegram.repository.NewsRepository;
import com.egemen.TweetBotTelegram.service.NewsService;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.cache.annotation.Cacheable;
import com.egemen.TweetBotTelegram.exception.ResourceNotFoundException;
import com.egemen.TweetBotTelegram.dto.NewsArticleDTO;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NewsServiceImpl implements NewsService {

    // Uses MediaStack API to fetch news
    private static final String MEDIASTACK_API_KEY = "your-key";
    private static final String MEDIASTACK_API_URL = "http://api.mediastack.com/v1/news";
    private final NewsRepository newsRepository;
    private final BotRepository botRepository;
    private final RestTemplate restTemplate;
    private final CircuitBreaker circuitBreaker;
    private final MeterRegistry meterRegistry;

    public NewsServiceImpl(
            NewsRepository newsRepository,
            BotRepository botRepository,
            RestTemplate restTemplate,
            CircuitBreakerRegistry circuitBreakerRegistry,
            MeterRegistry meterRegistry) {
        this.newsRepository = newsRepository;
        this.botRepository = botRepository;
        this.restTemplate = restTemplate;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("newsApi");
        this.meterRegistry = meterRegistry;
    }

    @Override
    @Cacheable(value = "news", key = "#botId") // Caches news results by botId to avoid redundant API calls
    public List<News> fetchAndSaveNews(Long botId) {
        // Start timing the operation for metrics
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            // Circuit breaker pattern to handle API failures gracefully
            return CircuitBreaker.decorateSupplier(circuitBreaker, () -> {
                // 1. Find the bot configuration
                Bot bot = botRepository.findById(botId)
                        .orElseThrow(() -> new ResourceNotFoundException("Bot not found with id: " + botId));

                // 2. Build and call the MediaStack API URL
                String url = buildMediaStackUrl();
                log.info("Fetching news from: {}", url);

                
                // 3. Make API call to get news
                ResponseEntity<NewsResponseDTO> response = restTemplate.getForEntity(url, NewsResponseDTO.class);
                
                // 4. Handle empty response
                if (response.getBody() == null || response.getBody().getArticles() == null) {
                    log.warn("No news articles found in API response.");
                    return Collections.<News>emptyList();
                }

                // 5. Process news articles and save to database
                List<News> articles = processNewsArticles(response.getBody().getArticles(), bot);
                log.info("Saving {} articles to the database.", articles.size());
                
                return (List<News>) newsRepository.saveAll(articles);
            }).get();
        } finally {
            // Record metrics about the operation
            sample.stop(Timer.builder("news.fetch.time")
                    .tag("botId", String.valueOf(botId))
                    .register(meterRegistry));
        }
    }

    // Helper method to build the MediaStack API URL
    private String buildMediaStackUrl() {
        return MEDIASTACK_API_URL + "?access_key=" + MEDIASTACK_API_KEY +
                "&categories=general&languages=en&limit=5";
    }

    // Process the raw news articles into our News entities
    private List<News> processNewsArticles(List<NewsArticleDTO> articles, Bot bot) {
        return articles.stream()
                .map(article -> createNewsFromArticle(article, bot))
                .collect(Collectors.toList());
    }

    // Convert a single news article DTO to our News entity
    private News createNewsFromArticle(NewsArticleDTO article, Bot bot) {
        Timestamp publishedAt = new Timestamp(System.currentTimeMillis());
        if (article.getPublishedAt() != null) {
            try {
                publishedAt = Timestamp.from(article.getPublishedAt().toInstant());
            } catch (Exception e) {
                log.warn("Failed to parse published date for article: {}", article.getTitle(), e);
            }
        }

        return new News(bot, article.getTitle(),
                article.getDescription() != null ? article.getDescription() : "",
                publishedAt);
    }
}