package com.egemen.TweetBotTelegram.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "generated_image_path")
    private String generatedImagePath;

    @Column(nullable = false)
    private boolean processed = false;

    @Column(nullable = false)
    private boolean posted = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
