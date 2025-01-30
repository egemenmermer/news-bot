package com.egemen.TweetBotTelegram.entity;

import com.egemen.TweetBotTelegram.enums.TweetStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tweets")
public class Tweets {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bot_id", nullable = false)
    private Bot botId;

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

    public Tweets(News news, String content, TweetStatus status) {
        this.news = news;
        this.content = content;
        this.status = status;
    }


}
