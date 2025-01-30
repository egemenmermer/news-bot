package com.egemen.TweetBotTelegram.service.Impl;

import com.egemen.TweetBotTelegram.dto.NewsResponseDTO;
import com.egemen.TweetBotTelegram.entity.Bot;
import com.egemen.TweetBotTelegram.entity.News;
import com.egemen.TweetBotTelegram.repository.BotRepository;
import com.egemen.TweetBotTelegram.repository.NewsRepository;
import com.egemen.TweetBotTelegram.service.NewsService;
import lombok.Builder;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Builder
public class NewsServiceImpl implements NewsService {

    private final String MEDIASTACK_API_KEY = "8c2e88b68bfd74df64a1b19241de9c22";

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private BotRepository botRepository;

    @Override
    public List<News> fetchAndSaveNews(Long botId) {

        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new RuntimeException("Bot not found with id: " + botId));

        String url = "http://api.mediastack.com/v1/news?access_key=" + MEDIASTACK_API_KEY +
                "&categories=general&languages=en&limit=5";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<NewsResponseDTO> response = restTemplate.getForEntity(url, NewsResponseDTO.class);

        List<News> articles = response.getBody().getArticles().stream()
                .map(article -> {

                    Timestamp publishedAt;
                    try {
                        LocalDateTime localDateTime = article.getPublishedAt();
                        publishedAt = Timestamp.valueOf(localDateTime);
                    } catch (Exception e) {
                        publishedAt = new Timestamp(System.currentTimeMillis());
                    }

                    return new News(bot, article.getTitle(),
                            article.getDescription() != null ? article.getDescription() : "",
                            publishedAt);
                })
                .collect(Collectors.toList());

        return newsRepository.saveAll(articles);
    }
}
