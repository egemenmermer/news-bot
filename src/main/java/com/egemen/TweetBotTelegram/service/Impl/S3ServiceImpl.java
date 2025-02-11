package com.egemen.TweetBotTelegram.service.Impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.egemen.TweetBotTelegram.service.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.InputStream;

@Service
public class S3ServiceImpl implements S3Service {
    
    private final AmazonS3 s3Client;
    
    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3ServiceImpl(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String uploadImage(InputStream imageData, String fileName) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/jpeg");
        
        s3Client.putObject(new PutObjectRequest(
            bucketName, 
            fileName, 
            imageData, 
            metadata
        ));

        return s3Client.getUrl(bucketName, fileName).toString();
    }

    @Override
    public void deleteImage(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
    }

    @Override
    public String getImageUrl(String fileName) {
        return s3Client.getUrl(bucketName, fileName).toString();
    }
}