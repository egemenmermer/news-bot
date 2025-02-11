package com.egemen.TweetBotTelegram.service;

import com.egemen.TweetBotTelegram.entity.InstagramPost;
import java.util.List;

public interface InstagramService {
    List<InstagramPost> getAllPosts();
    List<InstagramPost> getPendingPosts();
    InstagramPost getPost(Long id);
    InstagramPost publishPost(Long id);
    void deletePost(Long id);
    InstagramPost createPost(Long newsId, String imageUrl);
}