package com.virnect.data.api;

import com.virnect.data.ApiResponse;
import com.virnect.data.dto.feign.WorkspaceMemberInfoListResponse;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/remote")
public interface IMemberRestAPI {

    @ApiOperation(value = "Lookup Member Information List", notes = "워크스페이스 멤버 리스트를 조회하는 API 입니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filter", value = "사용자 필터(MASTER, MANAGER, MEMBER)", dataType = "string", allowEmptyValue = true, defaultValue = ""),
            @ApiImplicitParam(name = "page", value = "Page Index Number", dataType = "Integer", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "size", value = "Paging Data Size", dataType = "number", paramType = "query", defaultValue = "20"),
            @ApiImplicitParam(name = "sort", value = "Sort Option", paramType = "query", defaultValue = "role, desc"),
    })
    @GetMapping(value = "members/{workspaceId}")
    ResponseEntity<ApiResponse<WorkspaceMemberInfoListResponse>> getMembers(
            @PathVariable(name = "workspaceId") String workspaceId,
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size
    );

    @ApiImplicitParams({
            @ApiImplicitParam(name = "filter", value = "사용자 필터(MASTER, MANAGER, MEMBER)", dataType = "string", allowEmptyValue = true, defaultValue = ""),
            @ApiImplicitParam(name = "page", value = "Page Index Number", dataType = "Integer", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "size", value = "Paging Data Size", dataType = "number", paramType = "query", defaultValue = "20"),
            @ApiImplicitParam(name = "sort", value = "Sort Option", paramType = "query", defaultValue = "role, desc"),
    })
    @ApiOperation(value = "Lookup Invitable Member Information List", notes = "초대 가능한 워크스페이스 멤버 리스트를 조회하는 API 입니다.")
    @GetMapping(value = "members/{workspaceId}/{sessionId}/{userId}")
    ResponseEntity<ApiResponse<WorkspaceMemberInfoListResponse>> getMembers(
            @PathVariable(name = "workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            @PathVariable("userId") String userId,
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size
    );
}
