package com.virnect.content.infra.file.upload;

import com.netflix.discovery.converters.Auto;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-04-08
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@SpringBootTest
@ActiveProfiles("local")
class MinioUploadServiceTest {
    @Autowired
    MinioUploadService minioUploadService;

    @Test
    @DisplayName("파일 존재 여부")
    void getFile() {
    }

    @Test
    @DisplayName("")
    void base64ImageUpload() {
    }

    @Test
    @DisplayName("")
    void uploadByFileInputStream() {
    }

    @Test
    @DisplayName("")
    void copyByFileObject() {
    }
}