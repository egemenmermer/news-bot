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
    private final NewsService newsService;
    private final TweetService tweetService;
    private final BotRepository botRepository;

    public SocialMediaScheduler(NewsService newsService, TweetService tweetService, BotRepository botRepository) {
        this.newsService = newsService;
        this.tweetService = tweetService;
        this.botRepository = botRepository;
    }

    @Scheduled(fixedRateString = "${app.scheduler.fetch-news-rate:300000}")
    public void scheduleFetchNews() {
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

    @Scheduled(fixedRateString = "${app.scheduler.post-rate:600000}")
    public void schedulePostTweets() {
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