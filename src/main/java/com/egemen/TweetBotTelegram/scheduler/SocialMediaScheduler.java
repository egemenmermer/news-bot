package com.egemen.TweetBotTelegram.scheduler;

import com.egemen.TweetBotTelegram.service.NewsService;
import com.egemen.TweetBotTelegram.service.TweetService;
import com.egemen.TweetBotTelegram.repository.BotRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SocialMediaScheduler {
    // Core services for handling news and tweets
    private final NewsService newsService;
    private final TweetService tweetService;
    private final BotRepository botRepository;

    // Main scheduler that runs two primary tasks:
    
    @Scheduled(fixedRateString = "${app.scheduler.fetch-news-rate:300000}") // Runs every 5 minutes
    public void scheduleFetchNews() {
        // 1. Fetches news for each configured bot
        // 2. Saves new articles to database
        log.info("Starting scheduled news fetch");
        try {
            botRepository.findAll().forEach(bot -> {
                try {
                    newsService.fetchAndSaveNews(bot.getId());
                } catch (Exception e) {
                    log.error("Error fetching news for bot {}: {}", bot.getId(), e.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("Error in scheduleFetchNews: {}", e.getMessage());
        }
    }

    @Scheduled(fixedRateString = "${app.scheduler.post-rate:600000}") // Runs every 10 minutes
    public void schedulePostTweets() {
        // 1. Checks for failed posts and retries them
        // 2. Generates new tweets from unprocessed news
        // 3. Schedules them for posting
        log.info("Starting scheduled tweet posting");
        try {
            tweetService.retryFailedPosts();
            List<Map<String, Object>> tweets = tweetService.generateTweetsForUnprocessedNews();
            log.info("Generated {} new tweets", tweets.size());
        } catch (Exception e) {
            log.error("Error in schedulePostTweets: {}", e.getMessage());
        }
    }
}