package com.egemen.TweetBotTelegram.service.Impl;

import com.egemen.TweetBotTelegram.entity.News;
import com.egemen.TweetBotTelegram.entity.Tweets;
import com.egemen.TweetBotTelegram.enums.TweetStatus;
import com.egemen.TweetBotTelegram.repository.NewsRepository;
import com.egemen.TweetBotTelegram.repository.TweetsRepository;
import com.egemen.TweetBotTelegram.service.GeminiService;
import com.egemen.TweetBotTelegram.service.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class TweetServiceImpl implements TweetService {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private TweetsRepository tweetRepository;

    @Override
    public List<Map<String, Object>> generateTweetsForUnprocessedNews() {
        // 1. Gets unprocessed news articles
        // 2. Uses Gemini AI to summarize news
        // 3. Generates engaging tweets
        // 4. Saves tweets to database
        List<News> unprocessedNews = newsRepository.findByProcessedFalse();

        List<Tweets> tweets = unprocessedNews.stream()
                .map(news -> {
                    String summary = geminiService.summarizeNews(news.getTitle(), news.getContent());
                    String tweetContent = geminiService.generateTweet(summary);

                    news.setProcessed(true);
                    newsRepository.save(news);

                    return new Tweets(news.getBot(), news, tweetContent, TweetStatus.SCHEDULED);
                })
                .collect(Collectors.toList());

        List<Tweets> savedTweets = tweetRepository.saveAll(tweets);

        return savedTweets.stream()
                .map(tweet -> Map.<String, Object>of(
                        "id", tweet.getId(),
                        "news_id", tweet.getNews().getId(),
                        "bot_id", tweet.getBot().getId(),
                        "content", tweet.getContent(),
                        "status", tweet.getStatus().toString()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void schedulePost(Long tweetId, LocalDateTime scheduledTime) {
        Tweets tweet = tweetRepository.findById(tweetId)
            .orElseThrow(() -> new CustomException("Tweet not found", HttpStatus.NOT_FOUND, "TWEET_NOT_FOUND"));
        tweet.setScheduledAt(Timestamp.valueOf(scheduledTime));
        tweet.setStatus(TweetStatus.SCHEDULED);
        tweetRepository.save(tweet);
    }

    @Override
    public void retryFailedPosts() {
        // Handles failed tweet posts with retry mechanism
        // Maximum 3 retry attempts
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
