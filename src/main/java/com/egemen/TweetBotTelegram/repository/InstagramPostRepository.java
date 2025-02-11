package com.egemen.TweetBotTelegram.repository;

import com.egemen.TweetBotTelegram.entity.InstagramPost;
import com.egemen.TweetBotTelegram.enums.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InstagramPostRepository extends JpaRepository<InstagramPost, Long> {
    List<InstagramPost> findByStatus(PostStatus status);
    List<InstagramPost> findByStatusOrderByCreatedAtAsc(PostStatus status);
}