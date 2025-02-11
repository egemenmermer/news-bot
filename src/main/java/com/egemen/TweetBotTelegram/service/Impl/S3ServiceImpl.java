package com.egemen.TweetBotTelegram.service.Impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.egemen.TweetBotTelegram.service.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class S3ServiceImpl implements S3Service {
    private static final Logger log = LoggerFactory.getLogger(S3ServiceImpl.class);

    private final AmazonS3 s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3ServiceImpl(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String uploadImage(InputStream imageData, String fileName) {
        try {
            // Generate unique file name if not provided
            if (fileName == null || fileName.trim().isEmpty()) {
                fileName = generateUniqueFileName();
            }

            // Set metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpeg");

            // Upload to S3
            log.info("Uploading image to S3: {}", fileName);
            s3Client.putObject(new PutObjectRequest(
                bucketName,
                fileName,
                imageData,
                metadata
            ));

            // Return the public URL
            return s3Client.getUrl(bucketName, fileName).toString();

        } catch (Exception e) {
            log.error("Error uploading image to S3: {}", e.getMessage());
            throw new RuntimeException("Failed to upload image to S3", e);
        }
    }

    @Override
    public void deleteImage(String fileName) {
        try {
            log.info("Deleting image from S3: {}", fileName);
            s3Client.deleteObject(bucketName, fileName);
        } catch (Exception e) {
            log.error("Error deleting image from S3: {}", e.getMessage());
            throw new RuntimeException("Failed to delete image from S3", e);
        }
    }

    @Override
    public String getImageUrl(String fileName) {
        try {
            if (!s3Client.doesObjectExist(bucketName, fileName)) {
                log.error("Image not found in S3: {}", fileName);
                return null;
            }
            return s3Client.getUrl(bucketName, fileName).toString();
        } catch (Exception e) {
            log.error("Error getting image URL from S3: {}", e.getMessage());
            throw new RuntimeException("Failed to get image URL from S3", e);
        }
    }

    @Override
    public InputStream getImage(String fileName) {
        try {
            log.info("Fetching image from S3: {}", fileName);
            return s3Client.getObject(bucketName, fileName).getObjectContent();
        } catch (Exception e) {
            log.error("Error fetching image from S3: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch image from S3", e);
        }
    }

    private String generateUniqueFileName() {
        return "news-" + UUID.randomUUID().toString() + ".jpg";
    }
}