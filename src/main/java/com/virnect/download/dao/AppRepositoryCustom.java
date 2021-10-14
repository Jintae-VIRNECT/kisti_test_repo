package com.virnect.download.dao;

import java.util.List;
import java.util.Optional;

import com.querydsl.core.Tuple;

import com.virnect.download.domain.App;
import com.virnect.download.dto.domain.DeviceLatestVersionCodeDto;

/**
 * Project: PF-Download
 * DATE: 2020-05-18
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface AppRepositoryCustom {
	List<App> getActiveAppList(List<Long> deviceIds, List<Long> latestVersionCodes);

	Long getLatestVersionCodeByPackageName(String packageName);

	Optional<App> getLatestVersionAppInfoByPackageName(String packageName);

	long registerSigningKeyByPackageName(String packageName, String signingKey);

	List<App> findByPackageNameAndSignature(String packageName, String signature);

	List<DeviceLatestVersionCodeDto> getLatestVersionInfoPerDeviceOfProduct(String productName);
}
