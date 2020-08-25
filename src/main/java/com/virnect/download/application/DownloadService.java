package com.virnect.download.application;

import com.virnect.download.dao.AppRepository;
import com.virnect.download.dao.ProductRepository;
import com.virnect.download.domain.App;
import com.virnect.download.domain.Product;
import com.virnect.download.dto.response.AppInfoListResponse;
import com.virnect.download.dto.response.AppInfoResponse;
import com.virnect.download.exception.DownloadException;
import com.virnect.download.global.common.ApiResponse;
import com.virnect.download.global.error.ErrorCode;
import com.virnect.download.infra.file.donwload.S3FileDownloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DownloadService {
    private final S3FileDownloadService fileUploadService;
    private final AppRepository appRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ApiResponse<Boolean> downloadApp(String uuid) {
        App app = this.appRepository.findByUuid(uuid).orElseThrow(() -> new DownloadException(ErrorCode.ERR_NOT_FOUND_FILE));

        app.setAppDownloadCount(app.getAppDownloadCount() + 1);
        this.appRepository.save(app);
/*
        if (app.getDevice().getType().getName().equals("Google Play")) {
            //링크 리턴
            URI redirectUri = new URI(app.getAppUrl());
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(redirectUri);
            return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
        } else {
            String fileName = FilenameUtils.getName(app.getAppUrl());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attatchment; filename=\"" +
                    new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + "\"");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(this.fileUploadService.fileDownload(fileName));
        }*/
        return new ApiResponse<>(true);
    }

    public ApiResponse<Boolean> downloadGuide(String uuid) {
        App app = this.appRepository.findByUuid(uuid).orElseThrow(() -> new DownloadException(ErrorCode.ERR_NOT_FOUND_FILE));

        app.setGuideDownloadCount(app.getGuideDownloadCount() + 1);
        this.appRepository.save(app);
/*
        String fileName = FilenameUtils.getName(app.getGuideUrl());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attatchment; filename=\"" +
                new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .body(this.fileUploadService.fileDownload(fileName));*/
        return new ApiResponse<>(true);

    }

    public ApiResponse<AppInfoListResponse> getAppList(String productName, Locale locale) {
        Product product = productRepository.findByName(productName.toUpperCase())
                .orElseThrow(() -> new DownloadException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER));

        List<App> apps = this.appRepository.getAppList(product);
        Map<List<Object>, List<App>> result = apps.stream().collect(Collectors.groupingBy(app -> Arrays.asList(app.getDevice().getId(), app.getOs().getId())));

        List<AppInfoResponse> appInfoList = new ArrayList<>();
        result.forEach((objects, appList) -> {
            AppInfoResponse appInfo = modelMapper.map(appList.get(0), AppInfoResponse.class);
            appInfo.setDeviceName(appList.get(0).getDevice().getDisplayTitle());
            appInfo.setReleaseTime(appList.get(0).getCreatedDate());
            appInfo.setDeviceType(appList.get(0).getDevice().getType());
            appInfo.setVersion("v." + appList.get(0).getVersionName());
            appInfo.setImageUrl(appList.get(0).getImage());
            appInfo.setGuideUrl(appList.get(0).getGuideUrl());

            appInfoList.add(appInfo);
        });

        return new ApiResponse<>(new AppInfoListResponse(appInfoList));
    }

}
