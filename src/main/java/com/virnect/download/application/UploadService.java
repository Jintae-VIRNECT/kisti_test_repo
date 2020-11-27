package com.virnect.download.application;

import com.virnect.download.dao.DeviceRepository;
import com.virnect.download.dao.ProductRepository;
import com.virnect.download.domain.App;
import com.virnect.download.domain.Device;
import com.virnect.download.domain.Product;
import com.virnect.download.dto.request.TempAppUploadRequest;
import com.virnect.download.dto.response.AppInfoResponse;
import com.virnect.download.exception.AppServiceException;
import com.virnect.download.global.error.ErrorCode;
import com.virnect.download.infra.file.donwload.FileDownloadService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Project: PF-Download
 * DATE: 2020-11-27
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UploadService {
    private final ProductRepository productRepository;
    private final DeviceRepository deviceRepository;
    private final ModelMapper modelMapper;
    private final FileDownloadService fileService;

    public AppInfoResponse uploadApplication(TempAppUploadRequest tempAppUploadRequest) throws IOException {
        Device device = getDeviceInfo(tempAppUploadRequest.getDeviceType(), tempAppUploadRequest.getOperationSystem());
        Product product = getProductInfo(tempAppUploadRequest.getProductName());

        //업로드
        String fileName = generateAppName(
                tempAppUploadRequest.getVersionName(),
                tempAppUploadRequest.getProductName(),
                tempAppUploadRequest.getOperationSystem(),
                tempAppUploadRequest.getDeviceType(),
                LocalDate.now().toString(),
                FilenameUtils.getExtension(tempAppUploadRequest.getUploadAppFile().getOriginalFilename()));
        String appUrl = fileService.fileUpload(tempAppUploadRequest.getUploadAppFile(),fileName);

        App apps = App.builder()
                .uuid(generateAppUUID())
                .device(device)
                .product(product)
                .versionName(tempAppUploadRequest.getVersionName())
                .versionCode(Long.parseLong(tempAppUploadRequest.getVersionName().replaceAll(".", "")))
                .appUrl(appUrl)
                .imageUrl(tempAppUploadRequest.getImageUrl().getUrl())
                .guideUrl(tempAppUploadRequest.getGuideUrl().getUrl())
                .packageName("")
                .signature("")
                .appStatus(null)
                .appUpdateStatus(null)
                .build();
        AppInfoResponse appInfoResponse = modelMapper.map(apps, AppInfoResponse.class);
        return appInfoResponse;
    }

    private Device getDeviceInfo(String deviceType, String operationSystem) {
        return deviceRepository.findByTypeAndOs(deviceType, operationSystem)
                .orElseThrow(() -> new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL_DEVICE_INFO_NOT_FOUND));
    }

    private Product getProductInfo(String productName) {
        return productRepository.findByName(productName)
                .orElseThrow(() -> new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL_PRODUCT_INFO_NOT_FOUND));
    }

    private String generateAppUUID() {
        String[] tokens = UUID.randomUUID().toString().split("-");
        return tokens[1] + "-" + tokens[4];
    }

    private String generateAppName(String versionName, String productName, String osName,String deviceName,
                                   String releseDate ,String extionsion) {
        //명명규칙 ex_ v0.7.2_remote_android_realwear_20200724.apk
        if(!versionName.startsWith("v")){
            versionName = "v"+versionName;
        }
        return String.format("%s_%s_%s_%s_%s.%s", versionName, productName, osName, deviceName, releseDate, extionsion).toLowerCase();
    }

}
