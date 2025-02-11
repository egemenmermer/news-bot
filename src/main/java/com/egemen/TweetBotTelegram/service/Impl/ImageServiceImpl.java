package com.egemen.TweetBotTelegram.service.Impl;

import com.egemen.TweetBotTelegram.entity.News;
import com.egemen.TweetBotTelegram.service.ImageService;
import com.egemen.TweetBotTelegram.service.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

@Service
public class ImageServiceImpl implements ImageService {
    private static final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);
    
    @Value("${pexels.api.key}")
    private String pexelsApiKey;
    
    private final RestTemplate restTemplate;
    private final S3Service s3Service;
    
    public ImageServiceImpl(RestTemplate restTemplate, S3Service s3Service) {
        this.restTemplate = restTemplate;
        this.s3Service = s3Service;
    }

    @Override
    public String findImageForNews(News news) {
        try {
            // First try to use the news article's image if it exists
            if (news.getImageUrl() != null && isValidForInstagram(news.getImageUrl())) {
                return processImageForInstagram(news.getImageUrl());
            }
            
            // If no valid image, search Pexels using the news title
            String pexelsImage = searchPexelsImage(news.getTitle());
            if (pexelsImage != null) {
                return processImageForInstagram(pexelsImage);
            }
            
            log.warn("No suitable image found for news: {}", news.getId());
            return null;
        } catch (Exception e) {
            log.error("Error finding image for news {}: {}", news.getId(), e.getMessage());
            return null;
        }
    }

    @Override
    public String processImageForInstagram(String imageUrl) {
        try {
            InputStream imageStream = downloadImage(imageUrl);
            BufferedImage originalImage = ImageIO.read(imageStream);
            
            // Process image to meet Instagram requirements
            // Instagram prefers 1080x1080 square images
            BufferedImage processedImage = resizeImage(originalImage, 1080, 1080);
            
            // Convert to JPEG
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(processedImage, "jpg", outputStream);
            
            // Upload to S3
            String fileName = "instagram-" + System.currentTimeMillis() + ".jpg";
            return s3Service.uploadImage(new ByteArrayInputStream(outputStream.toByteArray()), fileName);
        } catch (Exception e) {
            log.error("Error processing image {}: {}", imageUrl, e.getMessage());
            return null;
        }
    }

    @Override
    public boolean isValidForInstagram(String imageUrl) {
        try {
            BufferedImage image = ImageIO.read(new URL(imageUrl));
            int width = image.getWidth();
            int height = image.getHeight();
            
            // Instagram minimum requirements
            return width >= 320 && height >= 320;
        } catch (Exception e) {
            log.error("Error validating image {}: {}", imageUrl, e.getMessage());
            return false;
        }
    }

    @Override
    public String searchPexelsImage(String query) {
        try {
            String url = "https://api.pexels.com/v1/search?query=" + query + "&per_page=1";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", pexelsApiKey);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
            );
            
            // TODO: Parse Pexels response and extract image URL
            return null;
        } catch (Exception e) {
            log.error("Error searching Pexels: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public InputStream downloadImage(String imageUrl) {
        try {
            byte[] imageBytes = restTemplate.getForObject(imageUrl, byte[].class);
            return new ByteArrayInputStream(imageBytes);
        } catch (Exception e) {
            log.error("Error downloading image {}: {}", imageUrl, e.getMessage());
            return null;
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        // TODO: Implement image resizing logic
        return originalImage;
    }
}