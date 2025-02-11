package com.egemen.TweetBotTelegram.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "instagram_posts")
public class InstagramPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "news_id")
    private News news;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "caption")
    private String caption;

    @Column(name = "post_status")
    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @Column(name = "instagram_post_id")
    private String instagramPostId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "posted_at")
    private LocalDateTime postedAt;

    
}   