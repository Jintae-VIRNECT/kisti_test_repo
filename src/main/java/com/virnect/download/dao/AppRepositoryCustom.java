package com.virnect.download.dao;

import java.util.List;
import java.util.Optional;

import com.virnect.download.domain.App;
import com.virnect.download.domain.Device;
import com.virnect.download.domain.OS;
import com.virnect.download.domain.Product;
import com.virnect.download.dto.domain.DeviceLatestVersionCodeDto;

/**
 * Project: PF-Download
 * DATE: 2020-05-18
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface AppRepositoryCustom {
	Long getLatestVersionCodeByPackageName(String packageName);

	Optional<App> getLatestVersionActiveAppInfoByPackageName(String packageName);

	long registerSigningKeyByPackageName(String packageName, String signingKey);

	List<App> getAppByPackageNameAndSignature(String packageName, String signature);

	List<DeviceLatestVersionCodeDto> getLatestVersionInfoPerDeviceOfProduct(String productName);

	App getActiveAppByDeviceLatestVersionCode(DeviceLatestVersionCodeDto deviceLatestVersionCodeDto);

	Optional<App> getAppByProductAndOsAndDevice(Product product, OS os, Device device);

	boolean existAppVersionCode(Device device, OS os, long versionCode);

	Long getLatestVersionByDeviceAndOs(Device device, OS os);
}
