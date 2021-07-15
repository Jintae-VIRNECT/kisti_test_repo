package com.virnect.serviceserver.serviceremote.application;

import com.virnect.data.application.license.LicenseRestService;
import com.virnect.data.dto.rest.LicenseInfoListResponse;
import com.virnect.data.dto.rest.LicenseInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.dto.constraint.LicenseConstants;
import com.virnect.data.dto.constraint.LicenseItem;
import com.virnect.data.dto.response.license.LicenseItemResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationService {

	private final LicenseRestService licenseRestService;

	public ApiResponse<LicenseItemResponse> getLicenseInfo(String workspaceId, String userId) {

		ApiResponse<LicenseInfoListResponse> licenseValidation = this.licenseRestService.getUserLicenseValidation(
			workspaceId, userId);
		if (licenseValidation.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
			return new ApiResponse<>(licenseValidation.getCode(), licenseValidation.getMessage());
		}

		LicenseInfoResponse currentLicense = null;
		for (LicenseInfoResponse licenseInfoResponse : licenseValidation.getData().getLicenseInfoList()) {
			if (licenseInfoResponse.getProductName().contains(LicenseConstants.PRODUCT_NAME)) {
				currentLicense = licenseInfoResponse;
			}
		}

		LicenseItem licenseItem = LicenseItem.ITEM_PRODUCT;
		if (currentLicense == null) {
			return new ApiResponse<>(ErrorCode.ERR_LICENSE_PRODUCT_VALIDITY);
		}

		if (!currentLicense.getStatus().equals(LicenseConstants.STATUS_USE)) {
			return new ApiResponse<>(ErrorCode.ERR_LICENSE_NOT_VALIDITY);
		}

		LicenseItemResponse licenseItemResponse = new LicenseItemResponse();
		licenseItemResponse.setItemName(licenseItem.getItemName());
		licenseItemResponse.setUserCapacity(licenseItem.getUserCapacity());

		return new ApiResponse<>(licenseItemResponse);
	}

}
