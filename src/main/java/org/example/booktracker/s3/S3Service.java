package org.example.booktracker.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    public S3Service(S3Client s3Client){
        this.s3Client = s3Client;
    }

    //upload file
    public String putObject(String objectKey, byte[] file){ //byte used in
        try {
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            s3Client.putObject(putOb, RequestBody.fromBytes(file));

            return s3Client.utilities().getUrl(b -> b.bucket(bucketName).key(objectKey)).toString();
        }catch (S3Exception e){
            throw new RuntimeException("Failed to upload to S3: " + e.awsErrorDetails().errorMessage());
        }
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
