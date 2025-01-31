package com.egemen.TweetBotTelegram.entity;

import com.egemen.TweetBotTelegram.enums.TweetStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tweets")
public class Tweets {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bot_id", nullable = false)
    private Bot bot;

    @OneToOne
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

    @Column(nullable = false, name = "content")
    private String content;

    @Column(nullable = false, name = "status")
    private TweetStatus status;

    @Column(name = "retry_count")
    private int retryCount;

    @Column(name = "scheduled_at")
    private Timestamp scheduledAt;

    @Column(name = "created_at")
    private Timestamp createdAt;

    public Tweets(Bot bot, News news, String content, TweetStatus status) {
        this.bot = bot;
        this.news = news;
        this.content = content;
        this.status = status;
        this.createdAt = Timestamp.valueOf(LocalDateTime.now());
    }

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
