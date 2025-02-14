package com.egemen.TweetBotTelegram.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstagramMediaDTO {
    private String id;
    
    @JsonProperty("media_type")
    private String mediaType;
    
    @JsonProperty("media_url")
    private String mediaUrl;
    
    private String status;
    
    @JsonProperty("permalink")
    private String permaLink;
}