package com.egemen.TweetBotTelegram.service.Impl;

import com.egemen.TweetBotTelegram.entity.InstagramPost;
import com.egemen.TweetBotTelegram.entity.News;
import com.egemen.TweetBotTelegram.enums.PostStatus;
import com.egemen.TweetBotTelegram.repository.InstagramPostRepository;
import com.egemen.TweetBotTelegram.service.InstagramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InstagramServiceImpl implements InstagramService {
    private static final Logger log = LoggerFactory.getLogger(InstagramServiceImpl.class);

    @Value("${instagram.access.token}")
    private String accessToken;

    private final InstagramPostRepository instagramPostRepository;
    private final RestTemplate restTemplate;
    private final String INSTAGRAM_API_URL = "https://graph.instagram.com/v12.0/";

    public InstagramServiceImpl(InstagramPostRepository instagramPostRepository, RestTemplate restTemplate) {
        this.instagramPostRepository = instagramPostRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public InstagramPost createPost(News news, String imageUrl) {
        InstagramPost post = new InstagramPost();
        post.setNews(news);
        post.setImageUrl(imageUrl);
        post.setCaption(generateCaption(news));
        post.setStatus(PostStatus.PENDING);
        post.setCreatedAt(LocalDateTime.now());
        return instagramPostRepository.save(post);
    }

    @Override
    public boolean publishPost(InstagramPost post) {
        try {
            // First, create a container
            String containerId = createMediaContainer(post.getImageUrl(), post.getCaption());
            if (containerId != null) {
                // Then publish the container
                String postId = publishContainer(containerId);
                if (postId != null) {
                    post.setInstagramPostId(postId);
                    post.setStatus(PostStatus.POSTED);
                    post.setPostedAt(LocalDateTime.now());
                    instagramPostRepository.save(post);
                    return true;
                }
            }
            post.setStatus(PostStatus.FAILED);
            instagramPostRepository.save(post);
            return false;
        } catch (Exception e) {
            log.error("Error publishing post {}: {}", post.getId(), e.getMessage());
            post.setStatus(PostStatus.FAILED);
            instagramPostRepository.save(post);
            return false;
        }
    }

    @Override
    public void handleFailedPosts() {
        List<InstagramPost> failedPosts = instagramPostRepository.findByStatus(PostStatus.FAILED);
        failedPosts.forEach(this::publishPost);
    }

    private String generateCaption(News news) {
        StringBuilder caption = new StringBuilder();
        caption.append("📰 ").append(news.getTitle()).append("\n\n");
        caption.append(news.getDescription()).append("\n\n");
        caption.append("#news #update #breaking #latestnews");
        return caption.toString();
    }

    private String createMediaContainer(String imageUrl, String caption) {
        String url = INSTAGRAM_API_URL + "me/media";
        // Implementation of Instagram Graph API call
        // Returns container ID
        return null; // TODO: Implement actual API call
    }

    private String publishContainer(String containerId) {
        String url = INSTAGRAM_API_URL + "me/media_publish";
        // Implementation of Instagram Graph API call
        // Returns post ID
        return null; // TODO: Implement actual API call
    }
}
