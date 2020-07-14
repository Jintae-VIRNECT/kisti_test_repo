package com.virnect.serviceserver.gateway.api;

import com.virnect.serviceserver.gateway.application.RemoteGatewayService;
import com.virnect.serviceserver.gateway.dto.request.PageRequest;
import com.virnect.serviceserver.gateway.dto.rest.UserInfoListResponse;
import com.virnect.serviceserver.gateway.global.common.ApiResponse;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequiredArgsConstructor
@RequestMapping("/media")
public class MemberRestController {
    private static final Logger log = LoggerFactory.getLogger(MemberRestController.class);
    private static final String TAG = "MemberRestController";

    private final RemoteGatewayService remoteGatewayService;

    private HttpHeaders getResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return responseHeaders;
    }

    @ApiOperation(value = "Lookup Member Information List", notes = "API for Member Information List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "search", value = "Search Term(Email / UserName)", dataType = "string", allowEmptyValue = true, defaultValue = "remote"),
            @ApiImplicitParam(name = "paging", value = "Pagination Status", dataType = "boolean", allowEmptyValue = true, defaultValue = "false"),
            @ApiImplicitParam(name = "size", value = "Paging Data Size", dataType = "Integer", paramType = "query", defaultValue = "2"),
            @ApiImplicitParam(name = "page", value = "Page Index Number", dataType = "Integer", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "sort", value = "Sort Option", paramType = "query", defaultValue = "createdDate, desc"),
    })
    @GetMapping(value = "members")
    public ResponseEntity<ApiResponse<UserInfoListResponse>> getMembers(@RequestParam(name = "search", required = false) String search,
                                                                        @RequestParam("paging") boolean paging,
                                                                        @ApiIgnore PageRequest pageRequest) {
        log.info(TAG, "getMembers");
        ApiResponse<UserInfoListResponse> apiResponse = this.remoteGatewayService.getUsers(search, paging, pageRequest);
        log.debug(TAG, apiResponse.toString());
        return ResponseEntity.ok(apiResponse);
    }
}
