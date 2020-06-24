package com.virnect.download.application;

import com.virnect.download.dao.AppRepository;
import com.virnect.download.domain.App;
import com.virnect.download.domain.Product;
import com.virnect.download.dto.response.AppInfoListResponse;
import com.virnect.download.dto.response.AppUploadResponse;
import com.virnect.download.exception.DownloadException;
import com.virnect.download.global.common.ApiResponse;
import com.virnect.download.global.error.ErrorCode;
import com.virnect.download.infra.file.S3FileUploadService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DownloadService {
    private final S3FileUploadService fileUploadService;
    private final AppRepository appRepository;
    private final ModelMapper modelMapper;
    private final MessageSource messageSource;

    public ApiResponse<AppUploadResponse> uploadFile(MultipartFile file) throws IOException {
        this.fileUploadService.upload(file);
        return null;
    }

    public ResponseEntity<Object> downloadApp(String uuid) throws IOException, URISyntaxException {
        App app = this.appRepository.findByUuid(uuid).orElseThrow(() -> new DownloadException(ErrorCode.ERR_NOT_FOUND_FILE));

        app.setAppDownloadCount(app.getAppDownloadCount() + 1);
        this.appRepository.save(app);

        if (app.getDevice().getType().equals("Google Play")) {
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
        }
    }

    public ResponseEntity<byte[]> downloadGuide(String uuid) throws IOException {
        App app = this.appRepository.findByUuid(uuid).orElseThrow(() -> new DownloadException(ErrorCode.ERR_NOT_FOUND_FILE));

        app.setGuideDownloadCount(app.getGuideDownloadCount() + 1);
        this.appRepository.save(app);

        String fileName = FilenameUtils.getName(app.getGuideUrl());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attatchment; filename=\"" +
                new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .body(this.fileUploadService.fileDownload(fileName));
    }

    public ApiResponse<AppInfoListResponse> getAppList(String productName, Locale locale) {
        Product product = Product.valueOf(productName.toUpperCase());
        List<App> apps = this.appRepository.getAppList(product);
        Map<List<Object>, List<App>> result = apps.stream().collect(Collectors.groupingBy(app -> Arrays.asList(app.getDevice().getId(), app.getOs().getId())));

        List<AppInfoListResponse.AppInfo> appInfoList = new ArrayList<>();
        result.forEach((objects, appList) -> {
            AppInfoListResponse.AppInfo appInfo = modelMapper.map(appList.get(0), AppInfoListResponse.AppInfo.class);
            appInfo.setDevice(messageSource.getMessage(appList.get(0).getDevice().getType(), null, locale));
            appInfo.setReleaseTime(appList.get(0).getCreatedDate());
            appInfo.setOs(appList.get(0).getOs().getName());
            appInfo.setVersion("v." + appList.get(0).getVersion());

            appInfo.setImageUrl(appList.get(0).getImage());
            appInfoList.add(appInfo);
        });

        return new ApiResponse<>(new AppInfoListResponse(appInfoList));
    }

}
