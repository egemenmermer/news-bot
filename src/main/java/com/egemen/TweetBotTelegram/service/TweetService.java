package com.egemen.TweetBotTelegram.service;

import com.egemen.TweetBotTelegram.entity.Tweets;

import java.util.List;
import java.util.Map;

public interface TweetService {

    List<Map<String, Object>> generateTweetsForUnprocessedNews();
}
