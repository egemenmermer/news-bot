package com.egemen.TweetBotTelegram.scheduler;

import com.egemen.TweetBotTelegram.service.NewsService;
import com.egemen.TweetBotTelegram.service.TweetService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SocialMediaScheduler {
    private final NewsService newsService;
    private final TweetService tweetService;

    public SocialMediaScheduler(NewsService newsService, TweetService tweetService) {
        this.newsService = newsService;
        this.tweetService = tweetService;
    }

    @Scheduled(fixedRateString = "${app.scheduler.fetch-news-rate:300000}")
    public void scheduleFetchNews() {
        // Implement fetch scheduling logic
    }

    @Scheduled(fixedRateString = "${app.scheduler.post-rate:600000}")
    public void schedulePostTweets() {
        // Implement posting logic
    }
}