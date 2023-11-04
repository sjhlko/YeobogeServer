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

/**
 * {@link S3FileUploadService} 구현체
 */
@Service
@RequiredArgsConstructor
public class S3FileUploadServiceImpl implements S3FileUploadService {
    private final String DEFAULT_URL_PREFIX = "https://";
    private final String FOLDER_NAME = "origin/";

    private final AmazonS3 s3Client;
    @Value("${S3_BUCKET_NAME}")
    private String bucketName;
    @Value("${S3_REGION}")
    private String region;

    @Override
    public String uploadFile(MultipartFile file) {
        String fileName = generateFileName(file);
        String key = FOLDER_NAME + fileName;
        try {
            s3Client.putObject(new PutObjectRequest(bucketName, key,
                    file.getInputStream(), getObjectMetadata(file)));
            return getImageThumbnailUrl(fileName);
        } catch (IOException e) {
            throw new AppException(UserErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    /**
     * 특정 파일의 {@link ObjectMetadata} 를 조회함
     *
     * @param file {@link ObjectMetadata}를 조회할 파일
     * @return 해당 파일의 {@link ObjectMetadata}
     */
    private ObjectMetadata getObjectMetadata(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        return objectMetadata;
    }

    /**
     * S3 버킷에 업로드할 파일의 이름을 생성함
     *
     * @param file S3 버킷에 업로드할 파일
     * @return 생성된 파일의 이름
     */
    private String generateFileName(MultipartFile file) {
        return UUID.randomUUID() + "-" + file.getOriginalFilename();
    }

    /**
     * S3 버킷에 업로드된 이미지가 리사이징되어 업로드될 주소의 URL을 반환함.
     *
     * @param fileName 이미지 파일명
     * @return 리사이징된 이미지가 업로드된 폴더의 이미지 URL
     */
    private String getImageThumbnailUrl(String fileName) {
        return DEFAULT_URL_PREFIX + bucketName + ".s3." + region + ".amazonaws.com/thumbnail/" + fileName;
    }
}
