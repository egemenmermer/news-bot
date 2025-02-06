package com.egemen.TweetBotTelegram.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bot_id", insertable = false, updatable = false)
    private Bot bot;

    @Column(nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "published_at")
    private Timestamp publishedAt;

    @Column(name = "processed")
    private boolean processed = false;

    @Column(name = "bot_id")
    private Long botId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public News(Bot bot, String title, String description, Timestamp publishedAt) {
        this.bot = bot;
        this.title = title;
        this.description = description;
        this.publishedAt = publishedAt;
    }
}
