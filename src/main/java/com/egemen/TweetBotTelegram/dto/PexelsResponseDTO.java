package com.egemen.TweetBotTelegram.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PexelsResponseDTO {
    private int page;
    private int perPage;
    private List<PhotoDTO> photos;
    
    @JsonProperty("total_results")
    private int totalResults;

    // Getters and setters

    public static class PhotoDTO {
        private int id;
        private int width;
        private int height;
        private String url;
        private String photographer;
        @JsonProperty("src")
        private PhotoSourceDTO source;

        // Getters and setters
    }

    public static class PhotoSourceDTO {
        private String original;
        private String large;
        private String medium;
        private String small;

        // Getters and setters
    }
}