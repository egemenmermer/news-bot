package com.egemen.TweetBotTelegram.service.Impl;

import com.egemen.TweetBotTelegram.dto.NewsArticleDTO;
import com.egemen.TweetBotTelegram.dto.NewsResponseDTO;
import com.egemen.TweetBotTelegram.entity.News;
import com.egemen.TweetBotTelegram.repository.NewsRepository;
import com.egemen.TweetBotTelegram.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {
    private static final Logger log = LoggerFactory.getLogger(NewsServiceImpl.class);

    @Value("${mediastack.api.key}")
    private String apiKey;

    private final NewsRepository newsRepository;
    private final RestTemplate restTemplate;
    private final String NEWS_API_URL = "http://api.mediastack.com/v1/news";

    public NewsServiceImpl(NewsRepository newsRepository, RestTemplate restTemplate) {
        this.newsRepository = newsRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<News> fetchLatestNews() {
        log.info("Fetching latest news");
        List<News> savedNews = new ArrayList<>();

        try {
            // Build URL with parameters
            String url = String.format("%s?access_key=%s&languages=en&limit=10", NEWS_API_URL, apiKey);
            
            ResponseEntity<NewsResponseDTO> response = restTemplate.getForEntity(url, NewsResponseDTO.class);
            
            if (response.getBody() != null && response.getBody().getArticles() != null) {
                for (NewsArticleDTO article : response.getBody().getArticles()) {
                    News news = convertToNews(article);
                    if (!newsExists(news)) {
                        savedNews.add(newsRepository.save(news));
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error fetching news: {}", e.getMessage());
        }

        return savedNews;
    }

    @Override
    public News processNewsImage(News news) {
        if (news.getImageUrl() != null && !news.isProcessed()) {
            try {
                // Mark as processed
                news.setProcessed(true);
                return newsRepository.save(news);
            } catch (Exception e) {
                log.error("Error processing image for news {}: {}", news.getId(), e.getMessage());
            }
        }
        return news;
    }

    @Override
    public void markAsPosted(News news) {
        news.setPosted(true);
        newsRepository.save(news);
    }

    private boolean newsExists(News news) {
        return newsRepository.existsByTitleAndDescription(news.getTitle(), news.getDescription());
    }

    private News convertToNews(NewsArticleDTO article) {
        News news = new News();
        news.setTitle(article.getTitle());
        news.setDescription(article.getDescription());
        news.setImageUrl(article.getImageUrl());
        news.setCreatedAt(LocalDateTime.now());
        return news;
    }
}