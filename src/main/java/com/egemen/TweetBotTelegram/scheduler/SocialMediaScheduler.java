package com.egemen.TweetBotTelegram.scheduler;

import com.egemen.TweetBotTelegram.service.NewsService;
import com.egemen.TweetBotTelegram.service.ImageService;
import com.egemen.TweetBotTelegram.service.InstagramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SocialMediaScheduler {
    private static final Logger log = LoggerFactory.getLogger(SocialMediaScheduler.class);

    private final NewsService newsService;
    private final ImageService imageService;
    private final InstagramService instagramService;

    public SocialMediaScheduler(
            NewsService newsService,
            ImageService imageService,
            InstagramService instagramService) {
        this.newsService = newsService;
        this.imageService = imageService;
        this.instagramService = instagramService;
    }

    @Scheduled(fixedRateString = "${app.scheduler.fetch-news-rate:300000}")
    public void scheduleFetchNews() {
        log.info("Starting scheduled news fetch");
        try {
            newsService.fetchLatestNews();
        } catch (Exception e) {
            log.error("Error in scheduleFetchNews: {}", e.getMessage());
        }
    }

    @Scheduled(fixedRateString = "${app.scheduler.post-rate:600000}")
    public void scheduleInstagramPosts() {
        log.info("Starting scheduled Instagram posting");
        try {
            instagramService.handleFailedPosts();
            // Process new posts
            newsService.getUnprocessedNews().forEach(news -> {
                try {
                    String imageUrl = imageService.findImageForNews(news);
                    if (imageUrl != null) {
                        instagramService.createPost(news, imageUrl);
                    }
                } catch (Exception e) {
                    log.error("Error processing news {}: {}", news.getId(), e.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("Error in scheduleInstagramPosts: {}", e.getMessage());
        }
    }
}