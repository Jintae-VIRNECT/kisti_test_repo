package com.virnect.serviceserver.api;


import com.virnect.service.ApiResponse;
import com.virnect.service.api.IValidationRestAPI;
import com.virnect.service.constraint.LicenseConstants;
import com.virnect.service.constraint.LicenseItem;
import com.virnect.service.dto.feign.LicenseInfoListResponse;
import com.virnect.service.dto.feign.LicenseInfoResponse;
import com.virnect.service.dto.service.response.CompanyInfoResponse;
import com.virnect.service.dto.service.response.LicenseItemResponse;
import com.virnect.service.error.ErrorCode;
import com.virnect.service.error.exception.RestServiceException;
import com.virnect.serviceserver.data.UtilDataRepository;
import com.virnect.serviceserver.application.license.LicenseRestService;
import com.virnect.serviceserver.utils.LogMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ValidationController implements IValidationRestAPI {
    private static final String TAG = ValidationController.class.getSimpleName();
    private static final String REST_LICENSE_PATH = "/remote/licenses";
    private static final String REST_COMPANY_PATH = "/remote/company";

    //private final DataRepository dataRepository;
    private final UtilDataRepository utilDataRepository;
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

        //todo: delete check user is valid
        //DataProcess<UserInfoResponse> userInfo = this.dataRepository.checkUserValidation(userId);
        //log.info("COMPANY INFO :: USER INFO :: {}", userInfo.getData().getDescription());

        return ResponseEntity.ok(
                this.utilDataRepository.loadCompanyInformation(workspaceId)
        );
    }

    @Override
    public ResponseEntity<ApiResponse<CompanyInfoResponse>> getCompanyInfoRequestHandler(int companyCode, String workspaceId, String userId) {
        LogMessage.formedInfo(
                TAG,
                "REST API: GET " + REST_COMPANY_PATH,
                "getCompanyInfoRequestHandler"
        );

        ApiResponse<CompanyInfoResponse> apiResponse = this.utilDataRepository.loadCompanyInformation(companyCode, workspaceId, userId);
        return ResponseEntity.ok(apiResponse);
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
