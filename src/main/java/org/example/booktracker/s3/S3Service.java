package org.example.booktracker.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.region.static}")
    private String region;

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    public S3Service(S3Client s3Client){
        this.s3Client = s3Client;
    }

    //upload file
    public String putObject(MultipartFile file, String folder){
        try {
            String originalFileName = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFileName);
            String uniqueFileName = generateUniqueFileName(fileExtension);
            String fileKey = folder + "/" + uniqueFileName;

            if (!isValidFile(file)){
                throw new RuntimeException("Invalid file type. Only JPG, PNG, and GIF are allowed.");
            }

            //check if bigger than 10MB
            if (file.getSize() > 10 * 1024 * 1024){
                throw new RuntimeException("File size too large.");
            }

            //upload
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .contentType(file.getContentType()) //important for images
                    .build();

            s3Client.putObject(putOb, RequestBody.fromBytes(file.getBytes()));

            logger.info("File uploaded successfully: {}", fileKey);
            return fileKey;
        }catch (S3Exception e){
            throw new RuntimeException("Failed to upload to S3: " + e.awsErrorDetails().errorMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFileUrl(String fileKey){
        if (fileKey == null || fileKey.isEmpty()){
            return null;
        }
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileKey);
    }

    //download file
    public byte[] getObject(String key){
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ResponseInputStream<GetObjectResponse> res = s3Client.getObject(getObjectRequest);

        try {
            return res.readAllBytes();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    //delete file
    public void deleteFile(String fileKey) {
        try {
            if (fileKey != null && !fileKey.isEmpty()) {
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileKey)
                        .build();

                s3Client.deleteObject(deleteObjectRequest);
                logger.info("File deleted successfully: {}", fileKey);
            }
        } catch (Exception e) {
            logger.error("Error deleting file from S3: {}", fileKey, e);
        }
    }

    private String generateUniqueFileName(String extension){
        return UUID.randomUUID().toString() + "-" + System.currentTimeMillis() + extension;
    }

    //gets .jpg, .png, etc
    private String getFileExtension(String fileName){
        if (fileName == null || !fileName.contains(".")){
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private boolean isValidFile(MultipartFile file){
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/jpg"));
    }
}
