package com.egemen.TweetBotTelegram.service;

import com.egemen.TweetBotTelegram.entity.Tweets;

import java.util.List;

public interface TweetService {

    List<Tweets> generateTweetsForUnprocessedNews();
}
