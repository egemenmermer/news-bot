package com.egemen.TweetBotTelegram.dto;

import java.util.List;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class NewsResponseDTO {
    @JsonProperty("data")
    private List<NewsArticleDTO> articles;
}
