package com.egemen.TweetBotTelegram.service.Impl;

import com.egemen.TweetBotTelegram.entity.InstagramPost;
import com.egemen.TweetBotTelegram.entity.News;
import com.egemen.TweetBotTelegram.enums.PostStatus;
import com.egemen.TweetBotTelegram.repository.InstagramPostRepository;
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

    private final InstagramPostRepository instagramPostRepository;
    private final NewsService newsService;

    public InstagramServiceImpl(InstagramPostRepository instagramPostRepository, NewsService newsService) {
        this.instagramPostRepository = instagramPostRepository;
        this.newsService = newsService;
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
        try {
            // TODO: Implement Instagram API integration
            log.info("Publishing post to Instagram: {}", id);
            post.setStatus(PostStatus.POSTED);
            post.setPostedAt(LocalDateTime.now());
            return instagramPostRepository.save(post);
        } catch (Exception e) {
            log.error("Error publishing post {}: {}", id, e.getMessage());
            post.setStatus(PostStatus.FAILED);
            post.setRetryCount(post.getRetryCount() + 1);
            instagramPostRepository.save(post);
            throw new RuntimeException("Failed to publish post", e);
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
