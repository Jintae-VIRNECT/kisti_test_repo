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
@ActiveProfiles("test")
class S3FileDownloadServiceTest {
    @Autowired
    S3FileDownloadService s3FileDownloadService;

    @Test
    @DisplayName("유효하지 않은 Range 파라미터 체크")
    void fileDownload_InvalidRange_ContentServiceException() {
        String filePath = "https://virnect-platform-qa.s3.ap-northeast-2.amazonaws.com/workspace/contents/36287a68-cf2f-4ec1-ac2f-1b2be7d9f527.Ares";
        String range = "0-10";
        ContentServiceException contentServiceException = assertThrows(ContentServiceException.class, () -> {
            s3FileDownloadService.fileDownload(filePath, range);
        });
        assertEquals(ErrorCode.ERR_INVALID_REQUEST_PARAMETER, contentServiceException.getErrorCode());
    }

    @Test
    @DisplayName("존재하지 않는 파일 부분 다운로드")
    void fileDownload_InvalidFile_ContentServiceException() {
        String filePath = "https://virnect-platform-qa.s3.ap-northeast-2.amazonaws.com/workspace/contents/36287a68-cf2f-4ec1-ac2f-1b2be7d9f5277.Ares";
        String range = "bytes=0-10";
        ContentServiceException contentServiceException = assertThrows(ContentServiceException.class, () -> {
            s3FileDownloadService.fileDownload(filePath, range);
        });
        assertEquals(ErrorCode.ERR_CONTENT_DOWNLOAD, contentServiceException.getErrorCode());

    }

    @Test
    @DisplayName("파일 바이트 수를 넘는 부분 다운로드")
    void fileDownload_OverRange_ContentServiceException() {
        String filePath = "https://virnect-platform-qa.s3.ap-northeast-2.amazonaws.com/workspace/contents/36287a68-cf2f-4ec1-ac2f-1b2be7d9f527.Ares";
        String range = "bytes=9252842-";
        ContentServiceException contentServiceException = assertThrows(ContentServiceException.class, () -> {
            s3FileDownloadService.fileDownload(filePath, range);
        });
        assertEquals(ErrorCode.ERR_CONTENT_DOWNLOAD, contentServiceException.getErrorCode());
    }

}