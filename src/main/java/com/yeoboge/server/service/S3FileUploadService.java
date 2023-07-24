package com.yeoboge.server.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.web.multipart.MultipartFile;

public interface S3FileUploadService {
    String uploadFile(MultipartFile file);

    ObjectMetadata getObjectMetadata(MultipartFile file);
    String generateFilename(MultipartFile file);
}
