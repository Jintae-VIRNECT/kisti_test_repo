package com.virnect.workspace.api;

import com.virnect.workspace.application.WorkspaceService;
import com.virnect.workspace.dto.request.UsersCreateRequest;
import com.virnect.workspace.dto.request.WorkspaceCreateRequest;
import com.virnect.workspace.dto.request.WorkspaceInviteRequest;
import com.virnect.workspace.dto.response.*;
import com.virnect.workspace.dto.rest.WorkspaceInviteRestResponse;
import com.virnect.workspace.exception.BusinessException;
import com.virnect.workspace.global.common.ApiResponse;
import com.virnect.workspace.global.error.ErrorCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-08
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: workspace service rest api controller
 */
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/workspaces")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Api(produces = MediaType.APPLICATION_JSON_VALUE, value = "워크스페이스 API", consumes = MediaType.APPLICATION_JSON_VALUE)
public class WorkspaceController {
    private final WorkspaceService workspaceService;

    /**
     * 워크스페이스 생성
     *
     * @param workspaceCreateRequest - 워크스페이스 정보(userId, description)
     * @param bindingResult
     * @return - 워크스페이스 객체
     */
    @ApiOperation(
            value = "워크스페이스 생성",
            notes = "워크스페이스 생성 성공 시 워크스페이스 정보를 반환합니다"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<WorkspaceCreateResponse>> createWorkspace(@RequestBody @Valid WorkspaceCreateRequest workspaceCreateRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BusinessException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<WorkspaceCreateResponse> apiResponse = this.workspaceService.createWorkspace(workspaceCreateRequest);
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 사용자가 속한 워크스페이스 조회
     *
     * @param userId - 사용자 uuid
     * @return - 워크스페이스 정보
     */
    @ApiOperation(
            value = "워크스페이스 조회",
            notes = "사용자가 소속되어 있는 워크스페이스 정보를 반환합니다."
    )
    @GetMapping()
    public ResponseEntity<ApiResponse<WorkspaceInfoListResponse>> getUserWorkspaces(@RequestParam("userId") String userId) {
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<WorkspaceInfoListResponse> apiResponse = this.workspaceService.getUserWorkspaces(userId);
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 사용자 조회(use 검색, 필터, 정렬 기능)
     *
     * @param workspaceId - 워크스페이스 uuid
     * @param userId      - 사용자 uuid
     * @param search      - 검색명
     * @param filter      - 필터명 (all User or Master User)
     * @param pageable    - 페이징 + 정렬(page : 몇페이지를 보여줄지(default 0), size : 한 페이지에 몇개 보여줄건지(default 20), sort : 뭐를 기준으로 어떻게 정렬할건지)
     * @return
     */
    @ApiOperation(
            value = "워크스페이스 멤버 검색",
            notes = "워크스페이스 멤버를 필터 또는 검색명을 걸어서 검색합니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "search", value = "검색어", dataType = "string", allowEmptyValue = true, defaultValue = ""),
            @ApiImplicitParam(name = "filter", value = "사용자 필터(ALL, MASTER, MEMBER)", dataType = "string", allowEmptyValue = true, defaultValue = ""),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "20"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "email,desc"),
    })
    @GetMapping("/{workspaceId}/members")
    public ResponseEntity<ApiResponse<MemberListResponse>> getMembers(@PathVariable("workspaceId") String workspaceId, @RequestParam("userId") String userId, @RequestParam(value = "search", required = false) String search, @RequestParam(value = "filter", required = false) String filter,  @ApiIgnore Pageable pageable) {
        if (!StringUtils.hasText(userId) || !StringUtils.hasText(workspaceId)) {
            throw new BusinessException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<MemberListResponse> apiResponse = this.workspaceService.getMembers(workspaceId, userId, search, filter, pageable);
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 워크스페이스 정보 조회
     *
     * @param workspaceId - 워크스페이스 uuid
     * @param userId      - 사용자 uuid
     * @return- 워크스페이스 정보
     */
    @ApiOperation(
            value = "워크스페이스 정보 조회",
            notes = "워크스페이스의 마스터인 유저가 해당 워크스페이스의 정보를 조회합니다."
    )
    @GetMapping("/{workspaceId}/info")
    public ResponseEntity<ApiResponse<WorkspaceInfoResponse>> getWorkspaceInfo(@PathVariable("workspaceId") String workspaceId, @RequestParam("userId") String userId) {
        if (!StringUtils.hasText(workspaceId)) {
            throw new BusinessException(ErrorCode.ERR_INVALID_VALUE);
        }
        ApiResponse<WorkspaceInfoResponse> apiResponse = this.workspaceService.getWorkspaceInfo(workspaceId, userId);
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 워크스페이스 멤버 초대
     *
     * @param workspaceId                - 초대할 워크스페이스 uuid
     * @param workspaceInviteRequestList - 워크스페이스 초대 정보
     * @return - 멤버 초대 성공 여부
     */
    @PostMapping("/{workspaceId}/invite")
    public ResponseEntity<ApiResponse<WorkspaceInviteRestResponse>> inviteWorkspace(@PathVariable("workspaceId") String workspaceId, @RequestParam("userId") String userId, @RequestBody @Valid WorkspaceInviteRequest workspaceInviteRequestList) {
        if (!StringUtils.hasText(workspaceId)) {
            throw new BusinessException(ErrorCode.ERR_INVALID_VALUE);
        }
        ApiResponse<WorkspaceInviteRestResponse> apiResponse = this.workspaceService.inviteWorkspace(workspaceId, userId, workspaceInviteRequestList);
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 워크스페이스 멤버 초대 수락
     *
     * @param workspaceId - 수락한 워크스페이스 uuid
     * @param userId      - 수락한 유저 uuid
     * @param code        - 초대 코드
     * @return - 멤버 초대 수락 성공 여부
     */
    @GetMapping("/{workspaceId}/invite/accept")
    public ResponseEntity<ApiResponse<WorkspaceInviteAcceptResponse>> inviteWorkspaceAccept(@PathVariable("workspaceId") String workspaceId, @RequestParam("userId") String userId, @RequestParam("code") String code) {
        if (!StringUtils.hasText(workspaceId)) {
            throw new BusinessException(ErrorCode.ERR_INVALID_VALUE);
        }
        ApiResponse<WorkspaceInviteAcceptResponse> apiResponse = this.workspaceService.inviteWorkspaceAccept(workspaceId, userId, code);
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 워크스페이스 멤버 생성
     *
     * @param workspaceId       - 대상 워크스페이스 uuid
     * @param userId            - 생성하는 유저 uuid
     * @param userCreateRequest - 생성 되는 userInfo List
     * @param bindingResult
     * @return - 멤버 생성 성공 여부
     */
    @PostMapping("/{workspaceId}/create")
    public ResponseEntity<ApiResponse<UsersCreateResponse>> createUsers(@PathVariable("workspaceId") String workspaceId, @RequestParam("userId") String userId, @RequestBody @Valid UsersCreateRequest userCreateRequest, BindingResult bindingResult) {
        if (!StringUtils.hasText(workspaceId) || bindingResult.hasErrors()) {
            throw new BusinessException(ErrorCode.ERR_INVALID_VALUE);
        }
        ApiResponse<UsersCreateResponse> apiResponse = this.workspaceService.createUsers(workspaceId, userId, userCreateRequest);
        return ResponseEntity.ok(apiResponse);
    }


}
