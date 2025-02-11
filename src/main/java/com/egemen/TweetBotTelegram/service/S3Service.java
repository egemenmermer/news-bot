package com.egemen.TweetBotTelegram.service;

import java.io.InputStream;

public interface S3Service {
    String uploadImage(InputStream imageData, String fileName);
    void deleteImage(String fileName);
    String getImageUrl(String fileName);
}