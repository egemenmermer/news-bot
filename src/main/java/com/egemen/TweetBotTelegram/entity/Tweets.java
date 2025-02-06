package com.egemen.TweetBotTelegram.entity;

import com.egemen.TweetBotTelegram.enums.TweetStatus;
import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "tweets")
public class Tweets {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bot_id", nullable = false)
    private Bot bot;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TweetStatus status;

    @Column(name = "retry_count")
    private int retryCount = 0;

    @Column(name = "scheduled_at")
    private Timestamp scheduledAt;

    @Column(name = "created_at")
    private Timestamp createdAt;

    // Constructor
    public Tweets(Bot bot, News news, String content, TweetStatus status) {
        this.bot = bot;
        this.news = news;
        this.content = content;
        this.status = status;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    // Default constructor
    public Tweets() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bot getBot() {
        return bot;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public TweetStatus getStatus() {
        return status;
    }

    public void setStatus(TweetStatus status) {
        this.status = status;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public Timestamp getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(Timestamp scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
