package com.egemen.TweetBotTelegram.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class NewsResponseDTO {
    private Pagination pagination;
    private List<NewsArticleDTO> data;

    @Data
    public static class Pagination {
        private int limit;
        private int offset;
        private int count;
        private int total;
    }
}
