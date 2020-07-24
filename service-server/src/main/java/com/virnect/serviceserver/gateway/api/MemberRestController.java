package com.virnect.serviceserver.gateway.api;

import com.virnect.serviceserver.gateway.application.RemoteGatewayService;
import com.virnect.serviceserver.gateway.dto.request.PageRequest;
import com.virnect.serviceserver.gateway.dto.rest.UserInfoListResponse;
import com.virnect.serviceserver.gateway.dto.rest.WorkspaceMemberInfoListResponse;
import com.virnect.serviceserver.gateway.global.common.ApiResponse;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote")
public class MemberRestController {
    private static final String TAG = "MemberRestController";
    private static String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
    private static final String REST_PATH = "/remote/member";
    private final RemoteGatewayService remoteGatewayService;

    private HttpHeaders getResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return responseHeaders;
    }

    @ApiOperation(value = "Lookup Member Information List", notes = "워크스페이스 멤버 리스트를 조회하는 API 입니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filter", value = "사용자 필터(MASTER, MANAGER, MEMBER)", dataType = "string", allowEmptyValue = true, defaultValue = ""),
            @ApiImplicitParam(name = "page", value = "Page Index Number", dataType = "Integer", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "size", value = "Paging Data Size", dataType = "number", paramType = "query", defaultValue = "20"),
            @ApiImplicitParam(name = "sort", value = "Sort Option", paramType = "query", defaultValue = "role, desc"),
    })
    @GetMapping(value = "members/{workspaceId}")
    public ResponseEntity<ApiResponse<WorkspaceMemberInfoListResponse>> getMembers(
            @PathVariable(name = "workspaceId") String workspaceId,
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size) {

        log.info("REST API: GET {}/{}", REST_PATH, workspaceId != null ? workspaceId.toString() : "{}");

        ApiResponse<WorkspaceMemberInfoListResponse> apiResponse = this.remoteGatewayService.getMembers(workspaceId, filter, page, size);
        log.debug(TAG, apiResponse.toString());
        return ResponseEntity.ok(apiResponse);
    }
}
