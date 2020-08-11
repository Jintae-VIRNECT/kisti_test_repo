package com.virnect.serviceserver.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote")
public class ValidationController {
    private static final String TAG = ValidationController.class.getSimpleName();
    private static String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
    private static final String REST_PATH = "/remote/licenses";

    //private final RemoteGatewayService remoteGatewayService;


    private HttpHeaders getResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return responseHeaders;
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

    /*@ApiOperation(value = "Service Push Message ", notes = "푸시 메시지를 발행하는 API 입니다.")
    @PostMapping(value = "message/push")
    public ResponseEntity<ApiResponse<PushResponse>> sendPushMessageHandler(
            @RequestBody @Valid PushSendRequest pushSendRequest,
            BindingResult result) {

        log.info("REST API: POST {}/message/push", REST_PATH);

        if(result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<PushResponse> response = this.remoteGatewayService.sendPushMessage(pushSendRequest);
        return ResponseEntity.ok(response);
    }*/
}
