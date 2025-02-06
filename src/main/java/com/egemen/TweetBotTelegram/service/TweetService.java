package com.egemen.TweetBotTelegram.service;

import com.egemen.TweetBotTelegram.entity.Tweets;
import com.egemen.TweetBotTelegram.enums.TweetStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface TweetService {

    List<Map<String, Object>> generateTweetsForUnprocessedNews();
    void schedulePost(Long tweetId, LocalDateTime scheduledTime);
    void retryFailedPosts();
    void updatePostStatus(Long tweetId, TweetStatus status);
}
