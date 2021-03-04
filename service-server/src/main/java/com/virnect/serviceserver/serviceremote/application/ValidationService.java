package com.virnect.serviceserver.serviceremote.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.application.license.LicenseRestService;
import com.virnect.data.dto.rest.LicenseInfoListResponse;
import com.virnect.data.dto.rest.LicenseInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.serviceserver.serviceremote.dto.constraint.LicenseConstants;
import com.virnect.serviceserver.serviceremote.dto.constraint.LicenseItem;
import com.virnect.serviceserver.serviceremote.dto.response.license.LicenseItemResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationService {

	private final LicenseRestService licenseRestService;

	public ApiResponse<LicenseItemResponse> getLicenseInfo(String workspaceId, String userId) {

		ApiResponse<LicenseItemResponse> responseData;

		ApiResponse<LicenseInfoListResponse> licenseValidation = this.licenseRestService.getUserLicenseValidation(
			workspaceId, userId);
		if (licenseValidation.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
			new ApiResponse<>(licenseValidation.getCode(), licenseValidation.getMessage());
		}

		LicenseInfoResponse currentLicense = null;
		for (LicenseInfoResponse licenseInfoResponse : licenseValidation.getData().getLicenseInfoList()) {
			if (licenseInfoResponse.getProductName().contains(LicenseConstants.PRODUCT_NAME)) {
				currentLicense = licenseInfoResponse;
			}
		}

		LicenseItem licenseItem = LicenseItem.ITEM_PRODUCT;
		if (currentLicense == null) {
			responseData = new ApiResponse<>(ErrorCode.ERR_LICENSE_PRODUCT_VALIDITY);
		} else {
			if (!currentLicense.getStatus().equals(LicenseConstants.STATUS_USE)) {
				responseData = new ApiResponse<>(ErrorCode.ERR_LICENSE_NOT_VALIDITY);
			} else {
				LicenseItemResponse licenseItemResponse = new LicenseItemResponse();
				licenseItemResponse.setItemName(licenseItem.getItemName());
				licenseItemResponse.setUserCapacity(licenseItem.getUserCapacity());
				responseData = new ApiResponse<>(licenseItemResponse);
			}
		}
		return responseData;
	}

}
