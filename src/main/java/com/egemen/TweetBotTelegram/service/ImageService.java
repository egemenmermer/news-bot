package com.egemen.TweetBotTelegram.service;

import com.egemen.TweetBotTelegram.entity.News;
import java.io.InputStream;

public interface ImageService {
    /**
     * Finds or generates an appropriate image for the news article
     * @param news The news article
     * @return URL of the image
     */
    String findImageForNews(News news);

    /**
     * Processes and optimizes image for Instagram
     * @param imageUrl URL of the original image
     * @return Path to the processed image
     */
    String processImageForInstagram(String imageUrl);

    /**
     * Validates if the image meets Instagram requirements
     * @param imageUrl URL of the image
     * @return true if valid, false otherwise
     */
    boolean isValidForInstagram(String imageUrl);

    /**
     * Searches for relevant images using Pexels API
     * @param query Search query
     * @return URL of the found image or null if none found
     */
    String searchPexelsImage(String query);

    /**
     * Downloads image from URL
     * @param imageUrl URL of the image
     * @return InputStream of the image
     */
    InputStream downloadImage(String imageUrl);
}