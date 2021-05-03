package com.virnect.download.application;

import com.virnect.download.dao.AppRepository;
import com.virnect.download.domain.App;
import com.virnect.download.dto.response.AppInfoListResponse;
import com.virnect.download.dto.response.AppInfoResponse;
import com.virnect.download.exception.DownloadException;
import com.virnect.download.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DownloadService {
    private final AppRepository appRepository;
    private final ModelMapper modelMapper;
    private final Environment environment;;


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

    public AppInfoListResponse getAppList(String productName, Locale locale) {
        List<App> apps = appRepository.getActiveAppList(productName.toUpperCase());
        Map<List<Object>, List<App>> result = apps.stream().collect(Collectors.groupingBy(app -> Arrays.asList(app.getDevice(), app.getOs())));
        List<AppInfoResponse> appInfoResponseList = new ArrayList<>();

        result.values().forEach(appList -> {
            Optional<App> optionalApp = appList.stream().findFirst();
            optionalApp.ifPresent(app -> {
                AppInfoResponse appInfoResponse = modelMapper.map(app, AppInfoResponse.class);
                if(environment.getActiveProfiles()[0].equals("production") && app.getDevice().getProduct().getName().equals("REMOTE") &&app.getDevice().getType().equals("MOBILE")){
                    appInfoResponse.setAppUrl("https://play.google.com/store/apps/details?id=com.virnect.remote.mobile2");
                }
                if(environment.getActiveProfiles()[0].equals("production") && app.getDevice().getProduct().getName().equals("VIEW") &&app.getDevice().getType().equals("MOBILE")){
                    appInfoResponse.setAppUrl("https://play.google.com/store/apps/details?id=com.VIRNECT.VIRNECTView");
                }
                appInfoResponse.setDeviceType(app.getDevice().getTypeDescription());
                if (StringUtils.hasText(locale.getLanguage()) && locale.getLanguage().equalsIgnoreCase("en")) {
                    appInfoResponse.setDeviceName(app.getDevice().getModelDescriptionEng());
                } else {
                    appInfoResponse.setDeviceName(app.getDevice().getModelDescription());
                }
                appInfoResponse.setReleaseTime(app.getCreatedDate());
                appInfoResponse.setVersion("v" + app.getVersionName());
                appInfoResponseList.add(appInfoResponse);
            });
        });
        return new AppInfoListResponse(appInfoResponseList);
    }

}
