package com.virnect.data.api;

import com.virnect.data.ApiResponse;
import com.virnect.data.dto.feign.WorkspaceMemberInfoListResponse;
import com.virnect.data.dto.response.MemberInfoListResponse;
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

    @ApiOperation(value = "Lookup Workspace Member Information List", notes = "워크스페이스 멤버 리스트를 조회하는 API 입니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filter", value = "사용자 필터(MASTER, MANAGER, MEMBER)", dataType = "string", allowEmptyValue = true, defaultValue = ""),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", dataType = "Integer", paramType = "query", defaultValue = "0"),
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

    @ApiOperation(value = "Lookup Remote Member Information List", notes = "본인을 제외한 워크스페이스 리모트 멤버 리스트를 조회하는 API 입니다.(동일한 사용자 정보 제외)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filter", value = "사용자 필터(MASTER, MANAGER, MEMBER)", dataType = "string", allowEmptyValue = true, defaultValue = ""),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", dataType = "Integer", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "size", value = "Paging Data Size", dataType = "number", paramType = "query", defaultValue = "20"),
            @ApiImplicitParam(name = "sort", value = "Sort Option", paramType = "query", defaultValue = "role, desc"),
    })
    @GetMapping(value = "members/{workspaceId}/{userId}")
    ResponseEntity<ApiResponse<MemberInfoListResponse>> getMembers(
            @PathVariable(name = "workspaceId") String workspaceId,
            @PathVariable(name = "userId") String userId,
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size
    );

    @ApiOperation(value = "Lookup Invitable Remote Member Information List", notes = "초대 가능한 워크스페이스 리모트 멤버 리스트를 조회하는 API 입니다.(원격협업 참가자 제외)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filter", value = "사용자 필터(MASTER, MANAGER, MEMBER)", dataType = "string", allowEmptyValue = true, defaultValue = ""),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", dataType = "Integer", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "size", value = "Paging Data Size", dataType = "number", paramType = "query", defaultValue = "20"),
            @ApiImplicitParam(name = "sort", value = "Sort Option", paramType = "query", defaultValue = "role, desc"),
    })
    @GetMapping(value = "members/{workspaceId}/{sessionId}/{userId}")
    ResponseEntity<ApiResponse<MemberInfoListResponse>> getMembers(
            @PathVariable(name = "workspaceId") String workspaceId,
            @PathVariable(name = "sessionId") String sessionId,
            @PathVariable(name = "userId") String userId,
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size
    );
}
