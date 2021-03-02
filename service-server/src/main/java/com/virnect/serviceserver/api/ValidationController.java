package com.virnect.serviceserver.api;

import java.io.IOException;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.remote.dto.response.license.LicenseItemResponse;
import com.virnect.remote.application.ValidationService;
import com.virnect.remote.dto.response.company.CompanyInfoResponse;
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
    private static final String REST_PATH = "/remote/validation";

    private final ValidationService validationService;
    private final RemoteServiceConfig config;

    @ApiOperation(value = "Service License Validity ", notes = "서비스 라이선스 유효성을 확인합니다.")
    @GetMapping(value = "licenses/{workspaceId}/{userId}")
    public ResponseEntity<ApiResponse<LicenseItemResponse>> getLicenseInfo(
        @PathVariable String workspaceId,
        @PathVariable String userId
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET "
                + REST_PATH + "/"
                + (workspaceId != null ? workspaceId : "{}") + "::"
                + (userId != null ? userId : "{}"),
            "getLicenseInfo"
        );

        assert workspaceId != null;
        if (workspaceId.isEmpty() || Objects.requireNonNull(userId).isEmpty()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<LicenseItemResponse> responseData = validationService.getLicenseInfo(workspaceId, userId);
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "Service Company Information", notes = "회사별 서비스 정보를 제공합니다.")
    @GetMapping(value = "company/{workspaceId}/{userId}")
    public ResponseEntity<ApiResponse<CompanyInfoResponse>> getCompanyInfo(
        @PathVariable String workspaceId,
        @PathVariable String userId
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET "
                + REST_PATH + "/"
                + (workspaceId != null ? workspaceId : "{}") + "::"
                + (userId != null ? userId : "{}"),
            "getCompanyInfo"
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

        String policyLocation = config.remoteServiceProperties.getServicePolicyLocation();
        //ApiResponse<CompanyInfoResponse> responseData = validationService.getCompanyInfo(workspaceId, userId, policyLocation);
        //ApiResponse<CompanyInfoResponse> responseData = validationService.getCompanyInfo(workspaceId, userId);
        ApiResponse<CompanyInfoResponse> responseData = validationService.getCompanyInfoByCompanyCode(workspaceId, userId, 1, policyLocation);
        return ResponseEntity.ok(responseData);
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
            "REST API: GET "
                + REST_PATH + "::"
                + "workspaceId:" + (workspaceId != null ? workspaceId : "{}") + "/"
                + "userId:" + (userId != null ? userId : "{}") + "/"
                + "companyCode:" + companyCode,
            "getCompanyInfoRequestHandler"
        );

        String policyLocation = config.remoteServiceProperties.getServicePolicyLocation();
        ApiResponse<CompanyInfoResponse> responseData = validationService.getCompanyInfoByCompanyCode(workspaceId, userId, companyCode, policyLocation);
        return ResponseEntity.ok(responseData);
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
