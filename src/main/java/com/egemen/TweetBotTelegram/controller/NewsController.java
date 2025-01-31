package com.egemen.TweetBotTelegram.controller;

import com.egemen.TweetBotTelegram.entity.News;
import com.egemen.TweetBotTelegram.service.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @PostMapping("/fetch")
    public ResponseEntity<List<News>> fetchNews(@RequestParam Long botId) {
        List<News> newsList = newsService.fetchAndSaveNews(botId);
        return ResponseEntity.ok(newsList);
    }
}