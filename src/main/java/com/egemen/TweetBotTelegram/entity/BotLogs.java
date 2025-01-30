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
@Table(name = "bot_logs")
public class BotLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bot_id", nullable = false)
    private Bot botId;

    @Column(nullable = false, name = "log_type")
    private LogType logType;

    @Column(nullable = false, name = "log_message")
    private String logMessage;

    @Column(nullable = false, name = "created_at")
    private Timestamp createdAt;

}
