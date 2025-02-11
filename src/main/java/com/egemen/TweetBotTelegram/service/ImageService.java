package com.egemen.TweetBotTelegram.service;

import com.egemen.TweetBotTelegram.entity.News;
import java.io.InputStream;

public interface ImageService {
    /**
     * Searches for a relevant image using the news title and content
     * @param news The news article to find an image for
     * @return URL of the found image, or null if no suitable image found
     */
    String findImageForNews(News news);

    /**
     * Processes an image by adding text overlay and applying template
     * @param imageUrl URL of the original image
     * @param newsTitle Title to be added to the image
     * @return InputStream of the processed image
     */
    InputStream processImage(String imageUrl, String newsTitle);

    /**
     * Saves the processed image to storage (S3)
     * @param news The associated news article
     * @param processedImage The processed image as InputStream
     * @return URL of the saved image
     */
    String saveProcessedImage(News news, InputStream processedImage);
}
