package com.virnect.content.infra.file.download;

import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.error.ErrorCode;
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
class MinioDownloadServiceTest {
    @Autowired
    MinioDownloadService minioDownloadService;

    @Test
    @DisplayName("유효하지 않은 Range 파라미터 체크")
    void fileDownload_InvalidRange_ContentServiceException() {
        String filePath = "https://192.168.6.3:2838/virnect-platform/workspace/contents/40d40ae4-0168-4027-b315-10d8de707ff1.Ares";
        String range = "0-10";
        ContentServiceException contentServiceException = assertThrows(ContentServiceException.class, () -> {
            minioDownloadService.fileDownload(filePath, range);
        });
        assertEquals(ErrorCode.ERR_INVALID_REQUEST_PARAMETER, contentServiceException.getErrorCode());
    }

    @Test
    @DisplayName("존재하지 않는 파일 부분 다운로드")
    void fileDownload_InvalidFile_ContentServiceException() {
        String filePath = "https://192.168.6.3:2838/virnect-platform/workspace/contents/40d40ae4-0168-4027-b315-10d8de707ff11.Ares";
        String range = "bytes=0-10";
        ContentServiceException contentServiceException = assertThrows(ContentServiceException.class, () -> {
            minioDownloadService.fileDownload(filePath, range);
        });
        assertEquals(ErrorCode.ERR_CONTENT_DOWNLOAD, contentServiceException.getErrorCode());

    }

    @Test
    @DisplayName("파일 바이트 수를 넘는 부분 다운로드")
    void fileDownload_OverRange_ContentServiceException() {
        String filePath = "https://192.168.6.3:2838/virnect-platform/workspace/contents/40d40ae4-0168-4027-b315-10d8de707ff1.Ares";
        String range = "bytes=1024-";
        ContentServiceException contentServiceException = assertThrows(ContentServiceException.class, () -> {
            minioDownloadService.fileDownload(filePath, range);
        });
        assertEquals(ErrorCode.ERR_CONTENT_DOWNLOAD, contentServiceException.getErrorCode());
    }

}