package com.egemen.TweetBotTelegram.service.Impl;

import com.egemen.TweetBotTelegram.dto.PexelsResponseDTO;
import com.egemen.TweetBotTelegram.entity.News;
import com.egemen.TweetBotTelegram.service.ImageService;
import com.egemen.TweetBotTelegram.service.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {
    private static final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);

    @Value("${pexels.api.key}")
    private String pexelsApiKey;

    private final RestTemplate restTemplate;
    private final S3Service s3Service;
    private static final String PEXELS_API_URL = "https://api.pexels.com/v1/search";
    private static final int IMAGE_WIDTH = 1080;  // Instagram recommended width
    private static final int IMAGE_HEIGHT = 1080; // Square format

    public ImageServiceImpl(RestTemplate restTemplate, S3Service s3Service) {
        this.restTemplate = restTemplate;
        this.s3Service = s3Service;
    }

    @Override
    public String findImageForNews(News news) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", pexelsApiKey);
            
            // Create search query from news title and description
            String searchQuery = extractSearchQuery(news);
            String url = PEXELS_API_URL + "?query=" + searchQuery + "&per_page=1&orientation=square";
            
            ResponseEntity<PexelsResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                PexelsResponseDTO.class
            );

            if (response.getBody() != null && !response.getBody().getPhotos().isEmpty()) {
                return response.getBody().getPhotos().get(0).getSource().getLarge();
            }
        } catch (Exception e) {
            log.error("Error finding image for news {}: {}", news.getId(), e.getMessage());
        }
        return null;
    }

    @Override
    public InputStream processImage(String imageUrl, String newsTitle) {
        try {
            // Download and resize image
            BufferedImage originalImage = ImageIO.read(new URL(imageUrl));
            BufferedImage resizedImage = resizeImage(originalImage);
            
            // Create new image with space for text
            BufferedImage processedImage = new BufferedImage(
                IMAGE_WIDTH,
                IMAGE_HEIGHT,
                BufferedImage.TYPE_INT_RGB
            );

            // Draw original image
            Graphics2D g2d = processedImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                               RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.drawImage(resizedImage, 0, 0, null);

            // Add semi-transparent overlay for better text visibility
            g2d.setColor(new Color(0, 0, 0, 0.5f));
            g2d.fillRect(0, IMAGE_HEIGHT - 300, IMAGE_WIDTH, 300);

            // Add text
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 40));
            drawWrappedText(g2d, newsTitle, 50, IMAGE_HEIGHT - 250, IMAGE_WIDTH - 100);

            g2d.dispose();

            // Convert to InputStream
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(processedImage, "jpg", os);
            return new ByteArrayInputStream(os.toByteArray());
        } catch (Exception e) {
            log.error("Error processing image: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public String saveProcessedImage(News news, InputStream processedImage) {
        String fileName = "news-" + UUID.randomUUID() + ".jpg";
        return s3Service.uploadImage(processedImage, fileName);
    }

    private String extractSearchQuery(News news) {
        // Extract key words from title and description
        String titleWords = news.getTitle().replaceAll("[^a-zA-Z0-9\\s]", "");
        return titleWords.split("\\s+")[0] + " " + titleWords.split("\\s+")[1];
    }

    private BufferedImage resizeImage(BufferedImage originalImage) {
        BufferedImage resizedImage = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, 
                                                     BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                          RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        
        // Calculate dimensions preserving aspect ratio
        double aspectRatio = (double) originalImage.getWidth() / originalImage.getHeight();
        int width = IMAGE_WIDTH;
        int height = IMAGE_HEIGHT;
        
        if (aspectRatio > 1) {
            width = (int) (height * aspectRatio);
        } else {
            height = (int) (width / aspectRatio);
        }
        
        // Center the image
        int x = (IMAGE_WIDTH - width) / 2;
        int y = (IMAGE_HEIGHT - height) / 2;
        
        g.drawImage(originalImage, x, y, width, height, null);
        g.dispose();
        
        return resizedImage;
    }

    private void drawWrappedText(Graphics2D g2d, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g2d.getFontMetrics();
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        
        for (String word : words) {
            if (fm.stringWidth(line + word) < maxWidth) {
                line.append(word).append(" ");
            } else {
                g2d.drawString(line.toString(), x, y);
                y += fm.getHeight();
                line = new StringBuilder(word + " ");
            }
        }
        g2d.drawString(line.toString(), x, y);
    }
}