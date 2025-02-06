package com.egemen.TweetBotTelegram.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NewsResponseDTO {
    @JsonProperty("data")
    private List<NewsArticleDTO> articles;

    public List<NewsArticleDTO> getArticles() {
        return articles;
    }

    public void setArticles(List<NewsArticleDTO> articles) {
        this.articles = articles;
    }
}
