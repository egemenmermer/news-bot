package com.egemen.TweetBotTelegram.dto;

import java.time.ZonedDateTime;
import lombok.Data;

@Data
public class NewsArticleDTO {
    private String title;
    private String description;
    private ZonedDateTime publishedAt;
} 