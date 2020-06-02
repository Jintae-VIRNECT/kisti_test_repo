package com.virnect.download.application;

import com.virnect.download.dao.AppRepository;
import com.virnect.download.domain.App;
import com.virnect.download.domain.Product;
import com.virnect.download.dto.response.AppInfoListResponse;
import com.virnect.download.dto.response.AppUploadResponse;
import com.virnect.download.exception.DownloadException;
import com.virnect.download.global.common.ApiResponse;
import com.virnect.download.global.error.ErrorCode;
import com.virnect.download.infra.file.FileUploadService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public ResponseEntity<byte[]> downloadApp(String id) throws IOException {
        App app = this.appRepository.findById(Long.parseLong(id)).orElseThrow(() -> new DownloadException(ErrorCode.ERR_NOT_FOUND_FILE));

        app.setAppDownloadCount(app.getAppDownloadCount() + 1);
        this.appRepository.save(app);
        return this.downloadFile(app.getAppUrl());
    }

    public ResponseEntity<byte[]> downloadGuide(String id) throws IOException {
        App app = this.appRepository.findById(Long.parseLong(id)).orElseThrow(() -> new DownloadException(ErrorCode.ERR_NOT_FOUND_FILE));

        app.setGuideDownloadCount(app.getGuideDownloadCount() + 1);
        this.appRepository.save(app);

        return this.downloadFile(app.getGuideUrl());
    }

    public ResponseEntity<byte[]> downloadFile(String fileUrl) throws IOException {
        String fileName = FilenameUtils.getName(fileUrl);
        HttpHeaders headers = new HttpHeaders();
        byte[] media;
        InputStream inputStream = new URL(fileUrl).openStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byte[] readBuffer = new byte[4096];
            while (inputStream.read(readBuffer, 0, readBuffer.length) != -1) {
                byteArrayOutputStream.write(readBuffer);
            }

            media = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new DownloadException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        } finally {
            inputStream.close();
            byteArrayOutputStream.close();
        }

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attatchment; filename=\"" +
                new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + "\"");
        return ResponseEntity.ok()
                .headers(headers)
                .body(media);
    }

    public ApiResponse<AppInfoListResponse> getAppList(String productName) {
        Product product = Product.valueOf(productName.toUpperCase());
        List<App> apps = this.appRepository.getAppList(product);
        Map<List<Object>, List<App>> result = apps.stream().collect(Collectors.groupingBy(app -> Arrays.asList(app.getDevice().getId(), app.getOs().getId())));

        List<AppInfoListResponse.AppInfo> appInfoList = new ArrayList<>();
        result.forEach((objects, appList) -> {
            AppInfoListResponse.AppInfo appInfo = modelMapper.map(appList.get(0), AppInfoListResponse.AppInfo.class);
            appInfo.setDevice(appList.get(0).getDevice().getName());
            appInfo.setReleaseTime(appList.get(0).getCreatedDate());
            appInfo.setOs(appList.get(0).getOs().getName());
            appInfo.setVersion("v." + appList.get(0).getVersion());
            appInfoList.add(appInfo);
        });

        return new ApiResponse<>(new AppInfoListResponse(appInfoList));
    }

}
