package com.virnect.serviceserver.serviceremote.api;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.serviceserver.global.config.RemoteServiceConfig;
import com.virnect.serviceserver.serviceremote.application.CompanyService;
import com.virnect.serviceserver.serviceremote.dto.request.company.CompanyRequest;
import com.virnect.serviceserver.serviceremote.dto.request.company.CompanyResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.utils.LogMessage;
import com.virnect.serviceserver.serviceremote.dto.response.company.CompanyInfoResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote")
public class CompanyRestController {

    private static final String TAG = CompanyRestController.class.getSimpleName();
    private static final String REST_PATH = "/remote/admin";

    private final CompanyService companyService;
    private final RemoteServiceConfig config;

    @ApiOperation(value = "Create Company Information ", notes = "회사별 서비스 정보를 생성 합니다.")
    @PostMapping(value = "admin/company")
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompanyRequestHandler(
        @RequestBody @Valid CompanyRequest companyRequest,
        BindingResult result
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: POST "
                + REST_PATH + "::"
                + companyRequest.toString(),
            "createCompanyRequestHandler"
        );
        if (result.hasErrors()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<CompanyResponse> responseData = companyService.createCompany(companyRequest);
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "Update Company Information ", notes = "생성한 회사 정보를 수정합니다.")
    @PutMapping(value = "admin/company")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateCompanyRequestHandler(
        @RequestBody @Valid CompanyRequest companyRequest,
        BindingResult result
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: POST "
                + REST_PATH + "::"
                + companyRequest.toString(),
            "updateCompanyRequestHandler"
        );
        if (result.hasErrors()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<CompanyResponse> responseData = companyService.updateCompany(companyRequest);
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
                + workspaceId + "::"
                + userId,
            "getCompanyInfo"
        );
        if (StringUtils.isBlank(workspaceId) || StringUtils.isBlank(userId)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        String policyLocation = config.remoteServiceProperties.getServicePolicyLocation();
        ApiResponse<CompanyInfoResponse> responseData = companyService.getCompanyInfo(
            workspaceId,
            userId,
            policyLocation
        );
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
        ApiResponse<CompanyInfoResponse> responseData = companyService.getCompanyInfoByCompanyCode(
                workspaceId,
                userId,
                companyCode,
                policyLocation
        );
        return ResponseEntity.ok(responseData);
    }
}
