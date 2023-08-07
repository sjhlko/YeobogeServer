package com.yeoboge.server.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.yeoboge.server.service.impl.S3FileUploadServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3FileUploadServiceTest {
    @InjectMocks
    private S3FileUploadServiceImpl s3FileUploadService;
    @Mock
    private AmazonS3 s3Client;
    @Test
    @DisplayName("파일 업로드 성공")
    void uploadFileSuccess() {
        // given
        S3FileUploadServiceImpl s3FileUploadServiceSpy = Mockito.spy(new S3FileUploadServiceImpl(s3Client));
        String filename = "file";
        MockMultipartFile file = new MockMultipartFile("file",
                "test.img", "png",
                "test file".getBytes(StandardCharsets.UTF_8));

        // when
        when(s3FileUploadServiceSpy.generateFilename(file)).thenReturn(filename);
        when(s3FileUploadServiceSpy.getObjectMetadata(file)).thenReturn(new ObjectMetadata());
        when(s3Client.putObject(any(PutObjectRequest.class))).thenReturn(new PutObjectResult());

        // then
        assertThat(s3FileUploadServiceSpy.uploadFile(file))
                .isInstanceOf(String.class);
    }


}