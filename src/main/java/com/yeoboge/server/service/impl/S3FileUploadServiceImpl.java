package com.yeoboge.server.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.yeoboge.server.enums.error.UserErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.service.S3FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3FileUploadServiceImpl implements S3FileUploadService {
    private final AmazonS3 s3Client;
    @Value("${S3_BUCKET_NAME}")
    private String bucketName;
    @Value("${S3_REGION}")
    private String region;
    private String defaultUrl = "https://";
    @Override
    public String uploadFile(MultipartFile file) {
        String fileName = generateFilename(file);
        try{
            s3Client.putObject(new PutObjectRequest(bucketName,fileName,file.getInputStream(),getObjectMetadata(file))) ;
            return defaultUrl + bucketName + ".s3." + region + ".amazonaws.com/" +fileName;
        } catch (IOException e) {
            throw new AppException(UserErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    @Override
    public ObjectMetadata getObjectMetadata(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        return objectMetadata;
    }


    @Override
    public String generateFilename(MultipartFile file) {
        return UUID.randomUUID() + "-" + file.getOriginalFilename();
    }


}
