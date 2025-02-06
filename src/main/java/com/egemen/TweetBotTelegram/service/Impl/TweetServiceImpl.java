package com.egemen.TweetBotTelegram.service.Impl;

import com.egemen.TweetBotTelegram.entity.News;
import com.egemen.TweetBotTelegram.entity.Tweets;
import com.egemen.TweetBotTelegram.enums.TweetStatus;
import com.egemen.TweetBotTelegram.repository.NewsRepository;
import com.egemen.TweetBotTelegram.repository.TweetsRepository;
import com.egemen.TweetBotTelegram.service.GeminiService;
import com.egemen.TweetBotTelegram.service.TweetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class TweetServiceImpl implements TweetService {
    private static final Logger log = LoggerFactory.getLogger(TweetServiceImpl.class);

    private final GeminiService geminiService;
    private final NewsRepository newsRepository;
    private final TweetsRepository tweetRepository;

    public TweetServiceImpl(GeminiService geminiService, NewsRepository newsRepository, TweetsRepository tweetRepository) {
        this.geminiService = geminiService;
        this.newsRepository = newsRepository;
        this.tweetRepository = tweetRepository;
    }

    @Override
    public List<Map<String, Object>> generateTweetsForUnprocessedNews() {
        List<News> unprocessedNews = newsRepository.findByProcessedFalse();
        return unprocessedNews.stream()
                .map(news -> {
                    Map<String, Object> result = new HashMap<>();
                    try {
                        String summary = geminiService.summarizeNews(news.getTitle(), news.getDescription());
                        String tweetContent = geminiService.generateTweet(summary);
                        
                        Tweets tweet = new Tweets(
                            news.getBot(),
                            news,
                            tweetContent,
                            TweetStatus.DRAFT
                        );
                        
                        tweet.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                        tweetRepository.save(tweet);
                        news.setProcessed(true);
                        newsRepository.save(news);
                        
                        result.put("success", true);
                        result.put("tweet", tweet);
                        return result;
                    } catch (Exception e) {
                        log.error("Error generating tweet for news {}: {}", news.getId(), e.getMessage());
                        result.put("success", false);
                        result.put("error", e.getMessage());
                        return result;
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public void schedulePost(Long tweetId, LocalDateTime scheduledTime) {
        Tweets tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new RuntimeException("Tweet not found"));
        tweet.setScheduledAt(Timestamp.valueOf(scheduledTime));
        tweet.setStatus(TweetStatus.SCHEDULED);
        tweetRepository.save(tweet);
    }

    @Override
    public void retryFailedPosts() {
        List<Tweets> failedTweets = tweetRepository.findByStatus(TweetStatus.FAILED);
        failedTweets.stream()
            .filter(tweet -> tweet.getRetryCount() < 3)
            .forEach(tweet -> {
                tweet.setRetryCount(tweet.getRetryCount() + 1);
                tweet.setStatus(TweetStatus.SCHEDULED);
                tweetRepository.save(tweet);
            });
    }

    @Override
    public void updatePostStatus(Long tweetId, TweetStatus status) {
        tweetRepository.updateStatus(tweetId, status);
    }
}
