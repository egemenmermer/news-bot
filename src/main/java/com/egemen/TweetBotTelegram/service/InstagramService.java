package com.egemen.TweetBotTelegram.service;

import com.egemen.TweetBotTelegram.entity.InstagramPost;
import com.egemen.TweetBotTelegram.entity.News;

public interface InstagramService {
    InstagramPost createPost(News news, String imageUrl);
    boolean publishPost(InstagramPost post);
    void handleFailedPosts();
}