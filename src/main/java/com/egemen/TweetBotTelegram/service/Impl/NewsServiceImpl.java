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
import org.springframework.web.client.ResourceNotFoundException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NewsServiceImpl implements NewsService {

    private static final String MEDIASTACK_API_KEY = "8c2e88b68bfd74df64a1b19241de9c22";
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
    @Cacheable(value = "news", key = "#botId")
    public List<News> fetchAndSaveNews(Long botId) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            return CircuitBreaker.decorateSupplier(circuitBreaker, () -> {
                Bot bot = botRepository.findById(botId)
                        .orElseThrow(() -> new ResourceNotFoundException("Bot not found with id: " + botId));

                String url = buildMediaStackUrl();
                log.info("Fetching news from: {}", url);

                ResponseEntity<NewsResponseDTO> response = restTemplate.getForEntity(url, NewsResponseDTO.class);
                
                if (response.getBody() == null || response.getBody().getArticles() == null) {
                    log.warn("No news articles found in API response.");
                    return Collections.emptyList();
                }

                List<News> articles = processNewsArticles(response.getBody().getArticles(), bot);
                log.info("Saving {} articles to the database.", articles.size());
                
                return newsRepository.saveAll(articles);
            }).get();
        } finally {
            sample.stop(Timer.builder("news.fetch.time")
                    .tag("botId", String.valueOf(botId))
                    .register(meterRegistry));
        }
    }

    private String buildMediaStackUrl() {
        return MEDIASTACK_API_URL + "?access_key=" + MEDIASTACK_API_KEY +
                "&categories=general&languages=en&limit=5";
    }

    private List<News> processNewsArticles(List<NewsArticleDTO> articles, Bot bot) {
        return articles.stream()
                .map(article -> createNewsFromArticle(article, bot))
                .collect(Collectors.toList());
    }

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