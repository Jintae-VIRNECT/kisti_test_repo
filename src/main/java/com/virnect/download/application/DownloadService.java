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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DownloadService {
    private final AppRepository appRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;


    //TODO: 응답 데이터 수정필요,,
    public Boolean downloadApp(String uuid) {
        App app = appRepository.findByUuid(uuid)
                .orElseThrow(() -> new DownloadException(ErrorCode.ERR_NOT_FOUND_FILE));
        app.setAppDownloadCount(app.getAppDownloadCount() + 1);
        appRepository.save(app);
        return true;
    }

    public Boolean downloadGuide(String uuid) {
        App app = appRepository.findByUuid(uuid)
                .orElseThrow(() -> new DownloadException(ErrorCode.ERR_NOT_FOUND_FILE));
        app.setGuideDownloadCount(app.getGuideDownloadCount() + 1);
        appRepository.save(app);
        return true;
    }

    //TODO: 다국어별 변환 필요할수도...
    public AppInfoListResponse getAppList(String productName, Locale locale) {
        Product product = productRepository.findByName(productName.toUpperCase())
                .orElseThrow(() -> new DownloadException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER));
        List<App> apps = appRepository.getAppList(product);

        List<AppInfoResponse> appInfoList = apps.stream().map(app -> {
            AppInfoResponse appInfo = modelMapper.map(app, AppInfoResponse.class);
            appInfo.setDeviceType(app.getDevice().getType().toString());
            appInfo.setDeviceName(app.getDevice().getDescription());
            appInfo.setReleaseTime(app.getCreatedDate());
            appInfo.setVersion(app.getVersionName());
            return appInfo;
        }).collect(Collectors.toList());
        return new AppInfoListResponse(appInfoList);
    }
}
