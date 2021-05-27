package com.virnect.serviceserver.serviceremote.api;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.utils.LogMessage;
import com.virnect.serviceserver.serviceremote.application.ValidationService;
import com.virnect.serviceserver.serviceremote.dto.response.license.LicenseItemResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote")
public class ValidationController {

    private static final String TAG = ValidationController.class.getSimpleName();
    private static final String REST_PATH = "/remote/validation";

    private final ValidationService validationService;

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
                + workspaceId + "/"
                + userId,
            "getLicenseInfo"
        );
        if (StringUtils.isBlank(workspaceId) || StringUtils.isBlank(userId)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<LicenseItemResponse> responseData = validationService.getLicenseInfo(workspaceId, userId);
        return ResponseEntity.ok(responseData);
    }

}
