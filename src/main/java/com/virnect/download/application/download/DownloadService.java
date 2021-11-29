package com.virnect.download.application.download;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.download.dao.AppRepository;
import com.virnect.download.domain.App;
import com.virnect.download.dto.domain.DeviceLatestVersionCodeDto;
import com.virnect.download.dto.response.AppInfoListResponse;
import com.virnect.download.dto.response.AppInfoResponse;
import com.virnect.download.exception.DownloadException;
import com.virnect.download.global.error.ErrorCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class DownloadService {
	private final AppRepository appRepository;
	private final ModelMapper modelMapper;

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
		List<DeviceLatestVersionCodeDto> latestAppVersionOfPerDeviceByProductName = appRepository.getLatestVersionInfoPerDeviceOfProduct(
			productName.toUpperCase()
		);

		List<Long> deviceIds = latestAppVersionOfPerDeviceByProductName
			.stream()
			.map(DeviceLatestVersionCodeDto::getDeviceId)
			.collect(Collectors.toList());

		List<Long> latestVersionCodes = latestAppVersionOfPerDeviceByProductName
			.stream()
			.map(DeviceLatestVersionCodeDto::getVersionCode)
			.collect(Collectors.toList());

		List<App> apps = appRepository.getActiveAppList(deviceIds, latestVersionCodes);
		List<AppInfoResponse> appInfoResponseList = apps.stream().map(app -> {
			AppInfoResponse appInfoResponse = modelMapper.map(app, AppInfoResponse.class);
			appInfoResponse.setDeviceType(app.getDevice().getTypeDescription());
			if (StringUtils.hasText(locale.getLanguage()) && locale.getLanguage().equalsIgnoreCase("en")) {
				appInfoResponse.setDeviceName(app.getDevice().getModelDescriptionEng());
			} else {
				appInfoResponse.setDeviceName(app.getDevice().getModelDescription());
			}
			appInfoResponse.setOs(app.getOs().getDescription());
			appInfoResponse.setReleaseTime(app.getCreatedDate());
			appInfoResponse.setVersion("v" + app.getVersionName());
			return appInfoResponse;
		}).collect(Collectors.toList());
		return new AppInfoListResponse(appInfoResponseList);
	}

}
