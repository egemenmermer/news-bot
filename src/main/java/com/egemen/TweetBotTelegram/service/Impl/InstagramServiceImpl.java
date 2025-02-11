package com.egemen.TweetBotTelegram.service.Impl;

import com.egemen.TweetBotTelegram.entity.InstagramPost;
import com.egemen.TweetBotTelegram.entity.News;
import com.egemen.TweetBotTelegram.enums.PostStatus;
import com.egemen.TweetBotTelegram.exception.InstagramApiException;
import com.egemen.TweetBotTelegram.repository.InstagramPostRepository;
import com.egemen.TweetBotTelegram.service.InstagramApiService;
import com.egemen.TweetBotTelegram.service.InstagramService;
import com.egemen.TweetBotTelegram.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InstagramServiceImpl implements InstagramService {
    private static final Logger log = LoggerFactory.getLogger(InstagramServiceImpl.class);

    @Value("${instagram.access.token}")
    private String instagramAccessToken;

    @Value("${instagram.max.retries:3}")
    private int maxRetries;

    private final InstagramPostRepository instagramPostRepository;
    private final NewsService newsService;
    private final InstagramApiService instagramApiService;

    public InstagramServiceImpl(InstagramPostRepository instagramPostRepository, NewsService newsService, InstagramApiService instagramApiService) {
        this.instagramPostRepository = instagramPostRepository;
        this.newsService = newsService;
        this.instagramApiService = instagramApiService;
    }

    @Override
    public List<InstagramPost> getAllPosts() {
        return instagramPostRepository.findAll();
    }

    @Override
    public List<InstagramPost> getPendingPosts() {
        return instagramPostRepository.findByStatus(PostStatus.PENDING);
    }

    @Override
    public InstagramPost getPost(Long id) {
        return instagramPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instagram post not found with id: " + id));
    }

    @Override
    public InstagramPost publishPost(Long id) {
        InstagramPost post = getPost(id);
        
        if (post.getRetryCount() >= maxRetries) {
            log.error("Max retry attempts reached for post {}", id);
            post.setStatus(PostStatus.FAILED);
            return instagramPostRepository.save(post);
        }

        try {
            // Upload media to Instagram
            String containerId = instagramApiService.uploadMedia(post.getImageUrl());
            
            // Wait for processing
            String status = instagramApiService.getMediaStatus(containerId);
            int attempts = 0;
            while ("IN_PROGRESS".equals(status) && attempts < 30) {
                Thread.sleep(1000);
                status = instagramApiService.getMediaStatus(containerId);
                attempts++;
            }
            
            if ("ERROR".equals(status) || attempts >= 30) {
                throw new InstagramApiException("Media processing failed or timeout");
            }
            
            // Publish the post
            String postId = instagramApiService.publishMedia(containerId, post.getCaption());
            
            post.setInstagramPostId(postId);
            post.setStatus(PostStatus.POSTED);
            post.setPostedAt(LocalDateTime.now());
            return instagramPostRepository.save(post);
            
        } catch (Exception e) {
            log.error("Error publishing post {}: {}", id, e.getMessage());
            post.setStatus(PostStatus.FAILED);
            post.setRetryCount(post.getRetryCount() + 1);
            instagramPostRepository.save(post);
            throw new InstagramApiException("Failed to publish post", e);
        }
    }

    @Override
    public void deletePost(Long id) {
        instagramPostRepository.deleteById(id);
    }

    @Override
    public InstagramPost createPost(Long newsId, String imageUrl) {
        News news = newsService.getNews(newsId);
        InstagramPost post = new InstagramPost();
        post.setNews(news);
        post.setImageUrl(imageUrl);
        post.setCaption(generateCaption(news));
        post.setStatus(PostStatus.PENDING);
        return instagramPostRepository.save(post);
    }

    private String generateCaption(News news) {
        return String.format("📰 %s\n\n%s\n\n#news #update #breaking",
                news.getTitle(),
                news.getDescription());
    }
}
