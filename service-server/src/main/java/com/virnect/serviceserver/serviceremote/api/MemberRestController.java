package com.virnect.serviceserver.serviceremote.api;

import javax.validation.Valid;

import com.virnect.data.domain.member.MemberAuthType;
import com.virnect.data.dto.response.member.RemoteGroupInfoListResponse;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.dto.request.member.GroupRequest;
import com.virnect.data.dto.response.ResultResponse;
import com.virnect.data.dto.response.member.RemoteGroupInfoResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoListResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.utils.LogMessage;
import com.virnect.serviceserver.serviceremote.application.MemberService;
import com.virnect.data.dto.response.member.MemberInfoListResponse;
import com.virnect.data.dto.response.member.MemberSecessionResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote")
public class MemberRestController {

    private static final String TAG = MemberRestController.class.getSimpleName();
    private static final String REST_PATH = "/remote/member";
    private final MemberService memberService;

    @ApiOperation(value = "Lookup Workspace Member Information List", notes = "워크스페이스 멤버 리스트를 조회하는 API 입니다.")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "filter", value = "사용자 필터(MASTER, MANAGER, MEMBER)", dataType = "string", allowEmptyValue = true),
        @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", dataType = "Integer", paramType = "query", defaultValue = "0"),
        @ApiImplicitParam(name = "size", value = "Paging Data Size", dataType = "number", paramType = "query", defaultValue = "20"),
        @ApiImplicitParam(name = "sort", value = "Sort Option", paramType = "query", defaultValue = "role, desc"),
    })
    @GetMapping(value = "members/{workspaceId}")
    public ResponseEntity<ApiResponse<WorkspaceMemberInfoListResponse>> getMembersByWorkspaceId(
        @PathVariable(name = "workspaceId") String workspaceId,
        @RequestParam(value = "filter", required = false) String filter,
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "page") int page,
        @RequestParam(value = "size") int size
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET "
                + REST_PATH + "/"
                + workspaceId + "::"
                + "search:" + search,
            "getMembersByWorkspaceId"
        );
        if (Strings.isBlank(workspaceId)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        //increase page number + 1, cause page index starts 0
        WorkspaceMemberInfoListResponse responseData = memberService.getMembers(
            workspaceId,
            filter,
            search,
            page + 1,
            size
        );
        return ResponseEntity.ok(new ApiResponse<>(responseData));
    }

    @ApiOperation(value = "Lookup Remote Member Information List", notes = "본인을 제외한 워크스페이스 리모트 멤버 리스트를 조회하는 API 입니다.(동일한 사용자 정보 제외)")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "filter", value = "사용자 필터(MASTER, MANAGER, MEMBER)", dataType = "string", allowEmptyValue = true),
        @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", dataType = "Integer", paramType = "query", defaultValue = "0"),
        @ApiImplicitParam(name = "size", value = "Paging Data Size", dataType = "number", paramType = "query", defaultValue = "20"),
        @ApiImplicitParam(name = "sort", value = "Sort Option", paramType = "query", defaultValue = "role, desc"),
        @ApiImplicitParam(name = "accessTypeFilter", value = "로그인상태 필터 여부", dataType = "boolean", allowEmptyValue = true, defaultValue = "false"),
    })
    @GetMapping(value = "members/{workspaceId}/{userId}")
    public ResponseEntity<ApiResponse<MemberInfoListResponse>> getMembersByWorkspaceIdAndUserId(
        @PathVariable(name = "workspaceId") String workspaceId,
        @PathVariable(name = "userId") String userId,
        @RequestParam(value = "filter", required = false) String filter,
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "page") int page,
        @RequestParam(value = "size") int size,
        @RequestParam(value = "accessTypeFilter", required = false) boolean accessTypeFilter
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET "
                + REST_PATH + "/"
                + workspaceId + "/"
                + userId + "::"
                + "search:" + search,
            "getMembersByWorkspaceIdAndUserId"
        );
        if (Strings.isBlank(workspaceId) || Strings.isBlank(userId)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<MemberInfoListResponse> responseData = memberService.getMembersExceptForMe(
            workspaceId,
            userId,
            filter,
            search,
            page,
            size,
            accessTypeFilter
        );    
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "Lookup Invitable Remote Member Information List", notes = "초대 가능한 워크스페이스 리모트 멤버 리스트를 조회하는 API 입니다.(원격협업 참가자 제외)")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "filter", value = "사용자 필터(MASTER, MANAGER, MEMBER)", dataType = "string", allowEmptyValue = true),
        @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", dataType = "Integer", paramType = "query", defaultValue = "0"),
        @ApiImplicitParam(name = "size", value = "Paging Data Size", dataType = "number", paramType = "query", defaultValue = "20"),
        @ApiImplicitParam(name = "sort", value = "Sort Option", paramType = "query", defaultValue = "role, desc"),
    })
    @GetMapping(value = "members/{workspaceId}/{sessionId}/{userId}")
    public ResponseEntity<ApiResponse<MemberInfoListResponse>> getMembersByWorkspaceIdAndSessionIdAndUserId(
        @PathVariable(name = "workspaceId") String workspaceId,
        @PathVariable(name = "sessionId") String sessionId,
        @PathVariable(name = "userId") String userId,
        @RequestParam(value = "filter", required = false) String filter,
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "page") int page,
        @RequestParam(value = "size") int size
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET "
                + REST_PATH + "/"
                + workspaceId + "/"
                + sessionId + "/"
                + userId + "::"
                + "search:" + search,
            "getMembersByWorkspaceIdAndSessionIdAndUserId"
        );
        if (Strings.isBlank(workspaceId) || Strings.isBlank(sessionId) || Strings.isBlank(userId)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        //increase page number + 1, cause page index starts 0
        MemberInfoListResponse responseData = memberService.getMembersInvitePossible(workspaceId,
            sessionId,
            userId,
            filter,
            search,
            page,
            size
        );
        return ResponseEntity.ok(new ApiResponse<>(responseData));
    }

    @ApiOperation(value = "Member Account Withdrawal")
    @DeleteMapping(value = "members/{userId}")
    public ResponseEntity<ApiResponse<MemberSecessionResponse>> deleteMembersByUserId(
        @PathVariable(name = "userId") String userId
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: DELETE "
                + REST_PATH + "/"
                + userId,
            "deleteMembersByUserId"
        );
        if (Strings.isBlank(userId)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        MemberSecessionResponse responseData = memberService.deleteMembersBySession(userId);
        return ResponseEntity.ok(new ApiResponse<>(responseData));
    }

    @ApiOperation(value = "[MASTER] Create member group", notes = "멤버 그룹을 생성합니다")
    @PostMapping(value = "members/group/{workspaceId}/{userId}")
    public ResponseEntity<ApiResponse<RemoteGroupInfoResponse>> createGroupByMaster(
        @PathVariable(name = "workspaceId") String workspaceId,
        @PathVariable(name = "userId") String userId,
        @RequestBody @Valid GroupRequest groupRequest
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: POST "
                + REST_PATH + "/"
                + workspaceId + "/"
                + userId + "::"
                + "groupRequest:" + groupRequest.toString(),
            "createGroupByMaster"
        );
        if (Strings.isBlank(workspaceId) || Strings.isBlank(userId)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<RemoteGroupInfoResponse> responseData = memberService.createGroup(workspaceId, userId, groupRequest, MemberAuthType.MASTER);
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "[NORMAL USER] Create member group", notes = "개인별 멤버 그룹을 생성합니다")
    @PostMapping(value = "members/my-group/{workspaceId}/{userId}")
    public ResponseEntity<ApiResponse<RemoteGroupInfoResponse>> createGroupByUserId(
        @PathVariable(name = "workspaceId") String workspaceId,
        @PathVariable(name = "userId") String userId,
        @RequestBody @Valid GroupRequest groupRequest
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: POST "
                + REST_PATH + "/"
                + workspaceId + "/"
                + userId + "::"
                + "groupRequest:" + groupRequest.toString(),
            "createGroupByUserId"
        );
        if (Strings.isBlank(workspaceId) || Strings.isBlank(userId)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<RemoteGroupInfoResponse> responseData = memberService.createGroup(workspaceId, userId, groupRequest, MemberAuthType.USER);
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "[MASTER] Get member groups", notes = "멤버 그룹을 조회합니다")
    @GetMapping(value = "members/group/{workspaceId}")
    public ResponseEntity<ApiResponse<RemoteGroupInfoListResponse>> getGroupsByMaster(
        @PathVariable(name = "workspaceId") String workspaceId,
        @RequestParam(name = "userId") String userId
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET "
                + REST_PATH + "/"
                + workspaceId + "/"
                + userId,
            "getGroupsByMaster"
        );
        if (Strings.isBlank(workspaceId)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<RemoteGroupInfoListResponse> responseData = memberService.getGroups(workspaceId, userId);
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "[MASTER] Get selected member group detail information", notes = "멤버그룹을 상세조회합니다")
    @GetMapping(value = "members/group/{workspaceId}/{groupId}")
    public ResponseEntity<ApiResponse<RemoteGroupInfoResponse>> getGroupsDetailInfoByMaster(
        @PathVariable(name = "workspaceId") String workspaceId,
        @PathVariable(name = "groupId") String groupId,
        @RequestParam(value = "filter", required = false) String filter,
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "accessTypeFilter", required = false) boolean accessTypeFilter
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET "
                + REST_PATH + "/"
                + workspaceId + "/"
                + groupId,
            "getGroupsDetailInfoByMaster"
        );
        if (Strings.isBlank(workspaceId) || Strings.isBlank(groupId)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<RemoteGroupInfoResponse> responseData = memberService.getGroup(
            workspaceId,
            groupId,
            filter,
            search,
            accessTypeFilter
        );
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "[NORMAL USER] Get member groups", notes = "개인별 멤버 그룹을 조회합니다")
    @GetMapping(value = "members/my-group/{workspaceId}")
    public ResponseEntity<ApiResponse<RemoteGroupInfoListResponse>> getGroupsByUserId(
        @PathVariable(name = "workspaceId") String workspaceId,
        @RequestParam(name = "userId") String userId
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET "
                + REST_PATH + "/"
                + workspaceId + "/"
                + userId,
            "getGroupsByUserId"
        );
        if (Strings.isBlank(workspaceId)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<RemoteGroupInfoListResponse> responseData = memberService.getGroups(workspaceId, userId);
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "[NORMAL USER] Get selected member group detail information", notes = "개인별 멤버그룹을 상세조회합니다")
    @GetMapping(value = "members/my-group/{workspaceId}/{groupId}")
    public ResponseEntity<ApiResponse<RemoteGroupInfoResponse>> getGroupDetailInfoByUserId(
        @PathVariable(name = "workspaceId") String workspaceId,
        @PathVariable(name = "groupId") String groupId,
        @RequestParam(value = "filter", required = false) String filter,
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "accessTypeFilter", required = false) boolean accessTypeFilter
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET "
                + REST_PATH + "/"
                + workspaceId + "/"
                + groupId,
            "getGroupDetailInfoByUserId"
        );
        if (Strings.isBlank(workspaceId) || Strings.isBlank(groupId)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<RemoteGroupInfoResponse> responseData = memberService.getGroup(
            workspaceId,
            groupId,
            filter,
            search,
            accessTypeFilter
        );
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "[MASTER] Update member group", notes = "멤버 그룹정보를 수정합니다")
    @PutMapping(value = "members/group/{workspaceId}/{userId}/{groupId}")
    public ResponseEntity<ApiResponse<RemoteGroupInfoResponse>> updateGroupByMaster(
        @PathVariable(name = "workspaceId") String workspaceId,
        @PathVariable(name = "userId") String userId,
        @PathVariable(name = "groupId") String groupId,
        @RequestBody @Valid GroupRequest groupRequest
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: PUT "
                + REST_PATH + "/"
                + workspaceId + "/"
                + userId + "::"
                + "groupRequest:" + groupRequest.toString(),
            "updateGroupByMaster"
        );
        if (Strings.isBlank(workspaceId)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<RemoteGroupInfoResponse> responseData = memberService.updateGroup(
            workspaceId,
            userId,
            groupId,
            groupRequest,
            MemberAuthType.MASTER
        );
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "[NORMAL USER] Update member group", notes = "개인별 멤버 그룹정보를 수정합니다")
    @PutMapping(value = "members/my-group/{workspaceId}/{userId}/{groupId}")
    public ResponseEntity<ApiResponse<RemoteGroupInfoResponse>> updateGroupByUserId(
        @PathVariable(name = "workspaceId") String workspaceId,
        @PathVariable(name = "userId") String userId,
        @PathVariable(name = "groupId") String groupId,
        @RequestBody @Valid GroupRequest groupRequest
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: PUT "
                + REST_PATH + "/"
                + workspaceId + "/"
                + userId + "::"
                + "groupRequest:" + groupRequest.toString(),
            "updateGroupByUserId"
        );
        if (Strings.isBlank(workspaceId)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<RemoteGroupInfoResponse> responseData = memberService.updateGroup(
            workspaceId,
            userId,
            groupId,
            groupRequest,
            MemberAuthType.USER
        );
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "[MASTER] Delete member group", notes = "멤버 그룹을 삭제합니다")
    @DeleteMapping(value = "members/group/{workspaceId}/{userId}/{groupId}")
    public ResponseEntity<ApiResponse<ResultResponse>> deleteGroupByMaster(
        @PathVariable(name = "workspaceId") String workspaceId,
        @PathVariable(name = "userId") String userId,
        @PathVariable(name = "groupId") String groupId
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: PUT "
                + REST_PATH + "/"
                + workspaceId + "/"
                + userId + "::"
                + "groupId:" + groupId,
            "deleteGroupByMaster"
        );
        if (Strings.isBlank(workspaceId)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ResultResponse> responseData = memberService.deleteGroup(workspaceId, userId, groupId, MemberAuthType.MASTER);
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "[NORMAL USER] Delete member group", notes = "개인별 멤버 그룹을 삭제합니다")
    @DeleteMapping(value = "members/my-group/{workspaceId}/{userId}/{groupId}")
    public ResponseEntity<ApiResponse<ResultResponse>> deleteGroupByUserId(
        @PathVariable(name = "workspaceId") String workspaceId,
        @PathVariable(name = "userId") String userId,
        @PathVariable(name = "groupId") String groupId
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: PUT "
                + REST_PATH + "/"
                + workspaceId + "/"
                + userId + "::"
                + "groupId:" + groupId,
            "deleteGroupByUserId"
        );
        if (Strings.isBlank(workspaceId)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ResultResponse> responseData = memberService.deleteGroup(workspaceId, userId, groupId, MemberAuthType.USER);
        return ResponseEntity.ok(responseData);
    }

}
