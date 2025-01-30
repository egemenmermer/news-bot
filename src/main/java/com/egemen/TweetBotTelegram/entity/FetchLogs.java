package com.egemen.TweetBotTelegram.entity;

import com.egemen.TweetBotTelegram.enums.LogType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "fetch_logs")
public class FetchLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bot_id", nullable = false)
    private Bot botId;

    @Column(nullable = false, name = "fetched_at")
    private Timestamp fetchedAt;

    @Column(nullable = false, name = "status")
    private FetchType status;

    @Column(nullable = false, name = "fetched_count")
    private int fetchedCount;

    @Column(name = "log_message")
    private String logMessage;

}