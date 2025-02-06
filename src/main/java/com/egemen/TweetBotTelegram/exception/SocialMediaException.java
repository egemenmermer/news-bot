package com.egemen.TweetBotTelegram.exception;

import org.springframework.http.HttpStatus;

public class SocialMediaException extends CustomException {
    public SocialMediaException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "SOCIAL_MEDIA_ERROR");
    }
}