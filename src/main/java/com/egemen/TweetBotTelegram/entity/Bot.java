package com.egemen.TweetBotTelegram.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bots")
public class Bot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "api_key")
    private String apiKey;

    @Column(name = "api_secret")
    private String apiSecret;

    @Column(name = "fetch_time")
    private Timestamp fetchTime;

    @Column(name = "post_time")
    private Timestamp postTime;

    @Column(name = "last_run")
    private Timestamp lastRun;


}

