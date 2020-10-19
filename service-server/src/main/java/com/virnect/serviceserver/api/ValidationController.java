package com.virnect.serviceserver.api;

import com.virnect.data.ApiResponse;
import com.virnect.data.api.IValidationRestAPI;
import com.virnect.data.constraint.CompanyConstants;
import com.virnect.data.constraint.LicenseConstants;
import com.virnect.data.constraint.LicenseItem;
import com.virnect.data.dao.SessionType;
import com.virnect.data.dto.feign.LicenseInfoListResponse;
import com.virnect.data.dto.feign.LicenseInfoResponse;
import com.virnect.data.dto.feign.UserInfoResponse;
import com.virnect.data.dto.response.CompanyInfoResponse;
import com.virnect.data.dto.response.LicenseItemResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.serviceserver.data.DataProcess;
import com.virnect.serviceserver.data.DataRepository;
import com.virnect.serviceserver.feign.service.LicenseRestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ValidationController implements IValidationRestAPI {
    private static final String TAG = ValidationController.class.getSimpleName();
    private static String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
    private static final String REST_LICENSE_PATH = "/remote/licenses";
    private static final String REST_COMPANY_PATH = "/remote/company";

    private final DataRepository dataRepository;
    private final LicenseRestService licenseRestService;

    @Override
    public ResponseEntity<ApiResponse<LicenseItemResponse>> getLicenseInfo(
            String workspaceId,
            String userId) {
        log.info("REST API: GET {}/{}/{}",
                REST_LICENSE_PATH,
                workspaceId != null ? workspaceId : "{}",
                userId != null ? userId : "{}");

        if (workspaceId.isEmpty() || userId.isEmpty()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<LicenseInfoListResponse> licenseValidation = this.licenseRestService.getUserLicenseValidation(workspaceId, userId);
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
        }
    }

    @Override
    public ResponseEntity<ApiResponse<CompanyInfoResponse>> getCompanyInfo(
            String workspaceId,
            String userId) {
        log.info("REST API: GET {}/{}/",
                REST_COMPANY_PATH,
                userId != null ? userId : "{}");

        if (userId.isEmpty()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        // check user is valid
        DataProcess<UserInfoResponse> userInfo = this.dataRepository.checkUserValidation(userId);

        log.info("COMPANY INFO :: USER INFO :: {}", userInfo.getData().getDescription());

        return ResponseEntity.ok(
                this.dataRepository.loadCompanyInformation(workspaceId)
        );

        /*if(userInfo.getData().getDescription().equals("KINTEXT_USER")) {
            log.info("COMPANY INFO :: USER INFO :: Current user is KINTEXT User");
            this.dataRepository.loadCompanyInformation(workspaceId);
        } else {

        }*/

        /*CompanyInfoResponse companyInfoResponse = new CompanyInfoResponse();
        companyInfoResponse.setCompanyCode(CompanyConstants.COMPANY_KINTEX);
        companyInfoResponse.setSessionType(SessionType.OPEN);
        companyInfoResponse.setTranslation(true);*/

       //return ResponseEntity.ok(new ApiResponse<>(companyInfoResponse));
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
