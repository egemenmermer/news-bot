package com.egemen.TweetBotTelegram.service;

import java.awt.image.BufferedImage;

public interface ImageGenerationService {
    /**
     * Generates a news image with text overlay
     * @param newsTitle The title of the news
     * @param newsContent The content of the news
     * @return Path to the generated image
     */
    String generateNewsImage(String newsTitle, String newsContent);
    
    /**
     * Creates an image prompt based on news content
     * @param title The news title
     * @param content The news content
     * @return Generated prompt for image creation
     */
    String createImagePrompt(String title, String content);
    
    /**
     * Adds text overlay to an image
     * @param originalImage The source image
     * @param newsTitle The text to overlay
     * @return Processed image with text overlay
     */
    BufferedImage addTextOverlay(BufferedImage originalImage, String newsTitle);
}