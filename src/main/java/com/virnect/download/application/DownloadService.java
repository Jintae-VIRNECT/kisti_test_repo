package com.virnect.download.application;

import com.virnect.download.dao.AppRepository;
import com.virnect.download.domain.App;
import com.virnect.download.domain.Product;
import com.virnect.download.dto.response.AppInfoResponse;
import com.virnect.download.dto.response.AppUploadResponse;
import com.virnect.download.exception.DownloadException;
import com.virnect.download.global.common.ApiResponse;
import com.virnect.download.global.error.ErrorCode;
import com.virnect.download.infra.file.FileUploadService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DownloadService {
    private final FileUploadService fileUploadService;
    private final AppRepository appRepository;
    private final ModelMapper modelMapper;

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
        Product product = Product.valueOf(productName.toUpperCase());
        App app = this.appRepository.findFirstByProductEqualsOrderByCreatedDateDesc(product);

        //db 체크
        if (app == null) {
            throw new DownloadException(ErrorCode.ERR_NOT_UPLOADED_FILE);
        }

        //count 증가
        app.setDownloadCount(app.getDownloadCount() + 1);
        this.appRepository.save(app);

        return new ApiResponse<>(app.getUrl());

    }

    public ApiResponse<AppInfoResponse> findFile(String productName) {
        Product product = Product.valueOf(productName.toUpperCase());
        App app = this.appRepository.findFirstByProductEqualsOrderByCreatedDateDesc(product);
        //db 체크
        if (app == null) {
            throw new DownloadException(ErrorCode.ERR_NOT_UPLOADED_FILE);
        }

        AppInfoResponse appInfoResponse = new AppInfoResponse();
        appInfoResponse.setProductName(app.getProduct().name());
        appInfoResponse.setReleaseTime(app.getCreatedDate());
        appInfoResponse.setVersion("v." + app.getVersion());
        appInfoResponse.setDownloadCount(app.getDownloadCount());
        appInfoResponse.setOs(app.getOs().name());
        appInfoResponse.setDevice(app.getDevice().getName());

        return new ApiResponse<>(appInfoResponse);
    }
}
