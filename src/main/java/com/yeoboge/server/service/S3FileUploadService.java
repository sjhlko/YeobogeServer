package com.yeoboge.server.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * S3 버킷에 이미지 업로드 관련한 메서드를 제공하는 인터페이스
 */
public interface S3FileUploadService {
    /**
     * S3 버킷에 이미지를 업로드함
     *
     * @param file 업로드할 파일
     * @return 업로드 한 이미지의 링크를 리턴함
     */
    String uploadFile(MultipartFile file);
}
