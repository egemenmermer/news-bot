package com.egemen.TweetBotTelegram.dto;

import java.time.ZonedDateTime;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class NewsArticleDTO {
    private String title;
    private String description;
    private ZonedDateTime publishedAt;
} 