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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {

    private static final String MEDIASTACK_API_KEY = "8c2e88b68bfd74df64a1b19241de9c22";
    private static final String MEDIASTACK_API_URL = "http://api.mediastack.com/v1/news";
    private static final Logger log = LogManager.getLogger(NewsServiceImpl.class);

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private BotRepository botRepository;

    @Override
    public List<News> fetchAndSaveNews(Long botId) {

        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new RuntimeException("Bot not found with id: " + botId));

        String url = MEDIASTACK_API_URL + "?access_key=" + MEDIASTACK_API_KEY +
                "&categories=general&languages=en&limit=5";

        log.info("Fetching news from: {}", url);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<NewsResponseDTO> response = restTemplate.getForEntity(url, NewsResponseDTO.class);

        log.info("API Response: {}", response.getBody());

        if (response.getBody() == null || response.getBody().getArticles() == null) {
            log.warn("No news articles found in API response.");
            return Collections.emptyList();
        }

        List<News> articles = response.getBody().getArticles().stream()
                .map(article -> {
                    Timestamp publishedAt = new Timestamp(System.currentTimeMillis());
                    if (article.getPublishedAt() != null) {
                        try {
                            publishedAt = Timestamp.from(article.getPublishedAt().toInstant());
                        } catch (Exception e) {
                            log.warn("Failed to parse published date, using current timestamp.", e);
                        }
                    }

                    return new News(bot, article.getTitle(),
                            article.getDescription() != null ? article.getDescription() : "",
                            publishedAt);
                })
                .collect(Collectors.toList());

        log.info("Saving {} articles to the database.", articles.size());

        return newsRepository.saveAll(articles);
    }
}