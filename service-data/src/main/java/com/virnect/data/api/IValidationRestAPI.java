package com.virnect.data.api;

import com.virnect.data.ApiResponse;
import com.virnect.data.dto.response.LicenseItemResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/remote")
public interface IValidationRestAPI {
    @ApiOperation(value = "Service License Validity ", notes = "서비스 라이선스 유효성을 확인합니다.")
    @GetMapping(value = "licenses/{workspaceId}/{userId}")
    ResponseEntity<ApiResponse<LicenseItemResponse>> getLicenseInfo(
            @PathVariable String workspaceId,
            @PathVariable String userId);

    /*@ApiOperation(value = "Service License Validity ", notes = "서비스 라이선스 유효성을 확인합니다.")
    @GetMapping(value = "licenses/{workspaceId}/{userId}")
    ResponseEntity<ApiResponse<LicenseItemResponse>> getLicenseInfo(
            @PathVariable String workspaceId,
            @PathVariable String userId);*/


}
