package com.yeoboge.server.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.yeoboge.server.domain.vo.response.MessageResponse;
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

    /**
     * 특정 파일의 object meta data를 조회함
     *
     * @param file object meta data를 조회할 파일
     * @return 해당 파일의 {@link ObjectMetadata}
     */
    ObjectMetadata getObjectMetadata(MultipartFile file);

    /**
     * S3 버킷에 업로드할 파일의 이름을 생성함
     *
     * @param file S3 버킷에 업로드할 파일
     * @return 생성된 파일의 이름
     */
    String generateFilename(MultipartFile file);
}
