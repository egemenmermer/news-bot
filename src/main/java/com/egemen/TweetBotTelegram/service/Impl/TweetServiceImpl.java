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
import java.util.stream.Collectors;

@Service
public class TweetServiceImpl implements TweetService {

    private GeminiServiceImpl geminiService;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private TweetsRepository tweetRepository;

    @Override
    public List<Tweets> generateTweetsForUnprocessedNews() {
        List<News> unprocessedNews = newsRepository.findByProcessedFalse();

        List<Tweets> tweets = unprocessedNews.stream()
                .map(news -> {
                    String summary = geminiService.summarizeNews(news.getTitle(), news.getContent());

                    String tweetContent = geminiService.generateTweet(summary);

                    news.setProcessed(true);
                    newsRepository.save(news);

                    return new Tweets(news, tweetContent, TweetStatus.SCHEDULED);
                })
                .collect(Collectors.toList());

        return tweetRepository.saveAll(tweets);
    }
}
