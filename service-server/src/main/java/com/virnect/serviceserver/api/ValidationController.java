package com.virnect.serviceserver.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.dto.response.license.LicenseItemResponse;
import com.virnect.remote.application.ValidationService;
import com.virnect.data.dto.response.company.CompanyInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.utils.LogMessage;
import com.virnect.serviceserver.global.config.RemoteServiceConfig;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote")
public class ValidationController {
    private static final String TAG = ValidationController.class.getSimpleName();
    private static final String REST_LICENSE_PATH = "/remote/licenses";
    private static final String REST_COMPANY_PATH = "/remote/company";

    //private final DataRepository dataRepository;
    //private final UtilDataRepository utilDataRepository;
    //private final LicenseRestService licenseRestService;

    private final ValidationService validationService;
    private final RemoteServiceConfig config;

    @ApiOperation(value = "Service License Validity ", notes = "서비스 라이선스 유효성을 확인합니다.")
    @GetMapping(value = "licenses/{workspaceId}/{userId}")
    public ResponseEntity<ApiResponse<LicenseItemResponse>> getLicenseInfo(
        @PathVariable String workspaceId,
        @PathVariable String userId
    ) {
        log.info(
            "REST API: GET {}/{}/{}",
            REST_LICENSE_PATH,
            workspaceId != null ? workspaceId : "{}",
            userId != null ? userId : "{}"
        );

        if (workspaceId.isEmpty() || userId.isEmpty()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<LicenseItemResponse> responseData = validationService.getLicenseInfo(workspaceId, userId);
        return ResponseEntity.ok(responseData);

		/*ApiResponse<LicenseInfoListResponse> licenseValidation = this.licenseRestService.getUserLicenseValidation(
			workspaceId, userId);
		if (licenseValidation.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
			return ResponseEntity.ok(new ApiResponse<>(licenseValidation.getCode(), licenseValidation.getMessage()));
		}

		LicenseInfoResponse currentLicense = null;
		for (LicenseInfoResponse licenseInfoResponse : licenseValidation.getData().getLicenseInfoList()) {
			if (licenseInfoResponse.getProductName().contains(LicenseConstants.PRODUCT_NAME)) {
				currentLicense = licenseInfoResponse;
			}
		}

		LicenseItem licenseItem = LicenseItem.ITEM_PRODUCT;
		if (currentLicense == null) {
			return ResponseEntity.ok(new ApiResponse<>(ErrorCode.ERR_LICENSE_PRODUCT_VALIDITY));
		} else {
			if (!currentLicense.getStatus().equals(LicenseConstants.STATUS_USE)) {
				return ResponseEntity.ok(new ApiResponse<>(ErrorCode.ERR_LICENSE_NOT_VALIDITY));
			} else {
				LicenseItemResponse licenseItemResponse = new LicenseItemResponse();
				licenseItemResponse.setItemName(licenseItem.getItemName());
				licenseItemResponse.setUserCapacity(licenseItem.getUserCapacity());
				return ResponseEntity.ok(new ApiResponse<>(licenseItemResponse));
			}
		}*/
    }

    @ApiOperation(value = "Service Company Information", notes = "회사별 서비스 정보를 제공합니다.")
    @GetMapping(value = "company/{workspaceId}/{userId}")
    public ResponseEntity<ApiResponse<CompanyInfoResponse>> getCompanyInfo(
        @PathVariable String workspaceId,
        @PathVariable String userId
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET " + REST_COMPANY_PATH + "/" + workspaceId + "/" + userId,
            "createCompanyRequestHandler"
        );

        if (userId == null || userId.isEmpty()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        if (workspaceId == null || workspaceId.isEmpty()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        String policyLocation = config.remoteServiceProperties.getServicePolicyLocation();
        ApiResponse<CompanyInfoResponse> responseData = validationService.getCompanyInfo(workspaceId, userId, policyLocation);
        return ResponseEntity.ok(responseData);

        //todo: delete check user is valid
        //DataProcess<UserInfoResponse> userInfo = this.dataRepository.checkUserValidation(userId);
        //log.info("COMPANY INFO :: USER INFO :: {}", userInfo.getData().getDescription());

		/*return ResponseEntity.ok(
			this.utilDataRepository.loadCompanyInformation(workspaceId)
		);*/
    }

    @ApiOperation(value = "Service Company Information", notes = "회사별 서비스 정보를 제공합니다.")
    @GetMapping(value = "company")
    public ResponseEntity<ApiResponse<CompanyInfoResponse>> getCompanyInfoRequestHandler(
        @RequestParam(name = "companyCode") int companyCode,
        @RequestParam(name = "workspaceId") String workspaceId,
        @RequestParam(name = "userId") String userId
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET " + REST_COMPANY_PATH,
            "getCompanyInfoRequestHandler"
        );

        String policyLocation = config.remoteServiceProperties.getServicePolicyLocation();
        ApiResponse<CompanyInfoResponse> responseData = validationService.getCompanyInfoByCompanyCode(workspaceId, userId, companyCode, policyLocation);
        return ResponseEntity.ok(responseData);

		/*ApiResponse<CompanyInfoResponse> apiResponse = this.utilDataRepository.loadCompanyInformation(
			companyCode, workspaceId, userId);
		return ResponseEntity.ok(apiResponse);*/
    }

    /*@ApiOperation(value = "Service License Validity ", notes = "서비스 라이선스 유효성을 확인합니다.")
    @GetMapping(value = "licenses/{workspaceId}/{userId}")
    public ResponseEntity<ApiResponse<LicenseInfoListResponse>> getLicenseInfo(
            @PathVariable String workspaceId,
            @PathVariable String userId) {

        log.info("REST API: GET {}/licenses/{}/{}", REST_PATH,
                workspaceId != null ? workspaceId.toString() : "{}",
                userId != null ? userId : "{}");
        if(workspaceId.isEmpty() || userId.isEmpty()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<LicenseInfoListResponse> response = this.remoteGatewayService.getLicenseValidity(workspaceId, userId);
        return ResponseEntity.ok(response);
    }*/
}
