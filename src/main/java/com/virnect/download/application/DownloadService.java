package com.virnect.download.application;

import com.virnect.download.global.common.ApiResponse;
import com.virnect.download.infra.file.FileUploadService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DownloadService {
    private final FileUploadService fileUploadService;
    public ApiResponse<Boolean> uploadFile(MultipartFile file) throws IOException {
        this.fileUploadService.upload(file);
        return null;
    }
}
