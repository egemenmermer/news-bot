package com.egemen.TweetBotTelegram.service;

import com.egemen.TweetBotTelegram.entity.News;
import java.util.List;

public interface NewsService {
    List<News> fetchLatestNews();
    News processNewsImage(News news);
    void markAsPosted(News news);
}
