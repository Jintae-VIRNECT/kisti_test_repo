package com.virnect.service.api;

import com.virnect.service.ApiResponse;
import com.virnect.service.dto.service.response.CompanyInfoResponse;
import com.virnect.service.dto.service.response.LicenseItemResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/remote")
public interface IValidationRestAPI {
    @ApiOperation(value = "Service License Validity ", notes = "서비스 라이선스 유효성을 확인합니다.")
    @GetMapping(value = "licenses/{workspaceId}/{userId}")
    ResponseEntity<ApiResponse<LicenseItemResponse>> getLicenseInfo(
            @PathVariable String workspaceId,
            @PathVariable String userId);

    /**
     * @Deprecated
     * @param workspaceId
     * @param userId
     * @return
     */
    @ApiOperation(value = "Service Company Information", notes = "회사별 서비스 정보를 제공합니다.")
    @GetMapping(value = "company/{workspaceId}/{userId}")
    ResponseEntity<ApiResponse<CompanyInfoResponse>> getCompanyInfo(
            @PathVariable String workspaceId,
            @PathVariable String userId);

    @ApiOperation(value = "Service Company Information", notes = "회사별 서비스 정보를 제공합니다.")
    @GetMapping(value = "company")
    ResponseEntity<ApiResponse<CompanyInfoResponse>> getCompanyInfoRequestHandler(
            @RequestParam(name = "companyCode") int companyCode,
            @RequestParam(name = "workspaceId") String workspaceId,
            @RequestParam(name = "userId") String userId);


}
