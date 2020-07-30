package com.virnect.serviceserver.gateway.api;

import com.virnect.serviceserver.gateway.application.RemoteGatewayService;
import com.virnect.serviceserver.gateway.dto.request.PushSendRequest;
import com.virnect.serviceserver.gateway.dto.request.RoomProfileUpdateRequest;
import com.virnect.serviceserver.gateway.dto.request.RoomRequest;
import com.virnect.serviceserver.gateway.dto.response.RoomResponse;
import com.virnect.serviceserver.gateway.dto.rest.LicenseInfoListResponse;
import com.virnect.serviceserver.gateway.dto.rest.PushResponse;
import com.virnect.serviceserver.gateway.exception.RemoteServiceException;
import com.virnect.serviceserver.gateway.global.common.ApiResponse;
import com.virnect.serviceserver.gateway.global.error.ErrorCode;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote")
public class ValidationController {
    private static final String TAG = ValidationController.class.getSimpleName();
    private static String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
    private static final String REST_PATH = "/remote/licenses";

    private final RemoteGatewayService remoteGatewayService;


    private HttpHeaders getResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return responseHeaders;
    }

    @ApiOperation(value = "Service License Validity ", notes = "서비스 라이선스 유효성을 확인합니다.")
    @GetMapping(value = "licenses/{workspaceId}/{userId}")
    public ResponseEntity<ApiResponse<LicenseInfoListResponse>> getLicenseInfo(
            @PathVariable String workspaceId,
            @PathVariable String userId) {

        log.info("REST API: GET {}/licenses/{}/{}", REST_PATH,
                workspaceId != null ? workspaceId.toString() : "{}",
                userId != null ? userId : "{}");
        if(workspaceId.isEmpty() || userId.isEmpty()) {
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<LicenseInfoListResponse> response = this.remoteGatewayService.getLicenseValidity(workspaceId, userId);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Service Push Message ", notes = "푸시 메시지를 발행하는 API 입니다.")
    @PostMapping(value = "message/push")
    public ResponseEntity<ApiResponse<PushResponse>> sendPushMessageHandler(
            @RequestBody @Valid PushSendRequest pushSendRequest,
            BindingResult result) {

        log.info("REST API: POST {}/message/push", REST_PATH);

        if(result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<PushResponse> response = this.remoteGatewayService.sendPushMessage(pushSendRequest);
        return ResponseEntity.ok(response);
    }

}
