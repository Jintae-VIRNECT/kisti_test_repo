package com.virnect.serviceserver.api;

import com.virnect.data.ApiResponse;
import com.virnect.data.api.IValidationRestAPI;
import com.virnect.data.constraint.LicenseConstants;
import com.virnect.data.constraint.LicenseItem;
import com.virnect.data.dto.feign.LicenseInfoListResponse;
import com.virnect.data.dto.feign.LicenseInfoResponse;
import com.virnect.data.dto.feign.PushResponse;
import com.virnect.data.dto.request.PushSendRequest;
import com.virnect.data.dto.response.LicenseItemResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.feign.service.LicenseRestService;
import com.virnect.serviceserver.data.DataProcess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ValidationController implements IValidationRestAPI {
    private static final String TAG = ValidationController.class.getSimpleName();
    private static String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
    private static final String REST_PATH = "/remote/licenses";

    private final LicenseRestService licenseRestService;

    @Override
    public ResponseEntity<ApiResponse<LicenseItemResponse>> getLicenseInfo(
            String workspaceId,
            String userId) {
        log.info("REST API: GET {}/{}/{}",
                REST_PATH,
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
