package com.egemen.TweetBotTelegram.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewsResponseDTO {
    private List<ArticleDTO> articles;

    public List<ArticleDTO> getArticles() {
        return articles;
    }
}
