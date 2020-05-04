package com.virnect.download.application;

import com.virnect.download.dao.AppRepository;
import com.virnect.download.domain.App;
import com.virnect.download.dto.response.AppResponse;
import com.virnect.download.dto.response.AppUploadResponse;
import com.virnect.download.exception.DownloadException;
import com.virnect.download.global.common.ApiResponse;
import com.virnect.download.global.error.ErrorCode;
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
    private final AppRepository appRepository;

    public ApiResponse<AppUploadResponse> uploadFile(MultipartFile file) throws IOException {
        this.fileUploadService.upload(file);
        return null;
    }
/*
    public ApiResponse<String> downloadFile(String fileName) {
        //TODO: 제품 체크

        String uploadedUrl = "https://download.virnect.com/" + fileName;

        if (!this.fileUploadService.doesFileExist(fileName)) {
            throw new DownloadException(ErrorCode.ERR_NOT_FOUND_FILE);
        }

        App app = this.appRepository.findByUrlEquals(uploadedUrl);

        if (app == null) {
            throw new DownloadException(ErrorCode.ERR_NOT_UPLOADED_FILE);
        }

        app.setDownloadCount(app.getDownloadCount() + 1);
        this.appRepository.save(app);

        return new ApiResponse<>(uploadedUrl);

    }*/


    public ApiResponse<String> downloadFile(String productName) {
        App app = this.appRepository.findFirstByProduct_NameOrderByCreatedDateDesc(productName);

        //db 체크
        if (app == null) {
            throw new DownloadException(ErrorCode.ERR_NOT_UPLOADED_FILE);
        }

        //count 증가
        app.setDownloadCount(app.getDownloadCount() + 1);
        this.appRepository.save(app);

        return new ApiResponse<>(app.getUrl());

    }

    public ApiResponse<AppResponse> findFile(String productName) {
        App app = this.appRepository.findFirstByProduct_NameOrderByCreatedDateDesc(productName);
        //db 체크
        if (app == null) {
            throw new DownloadException(ErrorCode.ERR_NOT_UPLOADED_FILE);
        }

        AppResponse appResponse = new AppResponse();
        appResponse.setProductName(app.getProduct().getName());
        appResponse.setReleaseTime(app.getCreatedDate());
        appResponse.setVersion("v."+app.getVersion());
        appResponse.setDownloadCount(app.getDownloadCount());

        return new ApiResponse<>(appResponse);
    }
}
