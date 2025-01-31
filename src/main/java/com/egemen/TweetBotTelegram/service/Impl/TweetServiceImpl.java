package com.egemen.TweetBotTelegram.service.Impl;

import com.egemen.TweetBotTelegram.entity.News;
import com.egemen.TweetBotTelegram.entity.Tweets;
import com.egemen.TweetBotTelegram.enums.TweetStatus;
import com.egemen.TweetBotTelegram.repository.NewsRepository;
import com.egemen.TweetBotTelegram.repository.TweetsRepository;
import com.egemen.TweetBotTelegram.service.GeminiService;
import com.egemen.TweetBotTelegram.service.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TweetServiceImpl implements TweetService {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private TweetsRepository tweetRepository;

    @Override
    public List<Map<String, Object>> generateTweetsForUnprocessedNews() {
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
}
