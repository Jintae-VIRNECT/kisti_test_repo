package com.virnect.workspace.api;

import com.virnect.workspace.application.WorkspaceService;
import com.virnect.workspace.dto.WorkspaceInfoDTO;
import com.virnect.workspace.dto.request.WorkspaceCreateRequest;
import com.virnect.workspace.dto.request.WorkspaceUpdateRequest;
import com.virnect.workspace.dto.response.*;
import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.common.ApiResponse;
import com.virnect.workspace.global.common.PageRequest;
import com.virnect.workspace.global.error.ErrorCode;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Locale;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-08
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: workspace service rest api controller
 */
@Slf4j
@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkspaceController {
    private final WorkspaceService workspaceService;

    @ApiOperation(
            value = "워크스페이스 시작하기",
            notes = "워크스페이스를 생성하는 기능입니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = " 유저 uuid", dataType = "string", paramType = "form", defaultValue = "uuid", required = true),
            @ApiImplicitParam(name = "name", value = "워크스페이스 이름(빈값 일 경우 닉네임's Workspace로 저장됩니다.)", dataType = "string", paramType = "form", defaultValue = "USER's Workspace", required = true),
            @ApiImplicitParam(name = "profile", value = "워크스페이스 프로필", dataType = "__file", paramType = "form"),
            @ApiImplicitParam(name = "description", value = "워크스페이스 설명", dataType = "string", paramType = "form", defaultValue = "워크스페이스 입니다.", required = true)
    })
    @PostMapping
    public ResponseEntity<ApiResponse<WorkspaceInfoDTO>> createWorkspace(
            @ModelAttribute @Valid WorkspaceCreateRequest workspaceCreateRequest, BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        WorkspaceInfoDTO response = workspaceService.createWorkspace(workspaceCreateRequest);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @ApiOperation(
            value = "워크스페이스 프로필 설정",
            notes = "생성된 워크스페이스의 프로필을 변경하는 기능입니다.(마스터 유저만 가능한 기능입니다.)"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 uuid", dataType = "string", paramType = "form", defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8", required = true),
            @ApiImplicitParam(name = "userId", value = "마스터 유저 uuid", dataType = "string", paramType = "form", defaultValue = "498b1839dc29ed7bb2ee90ad6985c608", required = true),
            @ApiImplicitParam(name = "name", value = "워크스페이스 이름", dataType = "string", paramType = "form", defaultValue = "USER's Workspace", required = true),
            @ApiImplicitParam(name = "profile", value = "워크스페이스 프로필", dataType = "__file", paramType = "form"),
            @ApiImplicitParam(name = "description", value = "워크스페이스 설명", dataType = "string", paramType = "form", defaultValue = "워크스페이스 입니다.", required = true)
    })
    @PutMapping
    public ResponseEntity<ApiResponse<WorkspaceInfoDTO>> setWorkspace(
            @ModelAttribute @Valid WorkspaceUpdateRequest workspaceUpdateRequest, BindingResult bindingResult,
            @Validated @ApiIgnore Locale locale
    ) {
        if (bindingResult.hasErrors()) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        WorkspaceInfoDTO response = workspaceService.setWorkspace(workspaceUpdateRequest, locale);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @ApiOperation(
            value = "내가 속한 워크스페이스 목록 조회",
            notes = "사용자가 마스터, 매니저, 멤버로 소속되어 있는 워크스페이스 정보를 조회합니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "유저 uuid", defaultValue = "498b1839dc29ed7bb2ee90ad6985c608", required = true),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "20"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "createdDate,desc"),
    })
    @GetMapping
    public ResponseEntity<ApiResponse<WorkspaceInfoListResponse>> getUserWorkspaces(
            @RequestParam("userId") String userId, @ApiIgnore PageRequest pageRequest
    ) {
        if (!StringUtils.hasText(userId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        WorkspaceInfoListResponse workspaceInfoListResponse = workspaceService.getUserWorkspaces(userId, pageRequest);
        return ResponseEntity.ok(new ApiResponse<>(workspaceInfoListResponse));
    }

    @ApiOperation(
            value = "워크스페이스 상세 정보 조회",
            notes = "워크스페이스 홈에서 워크스페이스의 정보를 조회합니다."
    )
    @GetMapping("/home/{workspaceId}")
    public ResponseEntity<ApiResponse<WorkspaceInfoResponse>> getWorkspaceDetailInfo(
            @PathVariable("workspaceId") String workspaceId
    ) {
        if (!StringUtils.hasText(workspaceId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        WorkspaceInfoResponse response = workspaceService.getWorkspaceDetailInfo(workspaceId);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @ApiOperation(
            value = "워크스페이스 히스토리 조회",
            notes = "워크스페이스 내에서 사용자의 활동을 조회합니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 uuid", dataType = "string", defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8", paramType = "path", required = true),
            @ApiImplicitParam(name = "userId", value = "사용자 uuid", dataType = "string", defaultValue = "498b1839dc29ed7bb2ee90ad6985c60", paramType = "query", required = true),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "20"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "createdDate,desc")
    })
    @GetMapping("/{workspaceId}/history")
    public ResponseEntity<ApiResponse<WorkspaceHistoryListResponse>> getWorkspaceHistory(
            @PathVariable("workspaceId") String workspaceId, @RequestParam("userId") String userId,
            @ApiIgnore PageRequest pageRequest
    ) {
        if (!StringUtils.hasText(workspaceId) || !StringUtils.hasText(userId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<WorkspaceHistoryListResponse> apiResponse = workspaceService.getWorkspaceHistory(
                workspaceId, userId, pageRequest.of());
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(
            value = "워크스페이스 라이선스 조회",
            notes = "워크스페이스 라이선스 정보를 조회합니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 uuid", dataType = "string", defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8", paramType = "path", required = true)
    })
    @GetMapping("/{workspaceId}/license")
    public ResponseEntity<ApiResponse<WorkspaceLicenseInfoResponse>> getWorkspaceLicenseInfo(
            @PathVariable("workspaceId") String workspaceId
    ) {
        if (!StringUtils.hasText(workspaceId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        WorkspaceLicenseInfoResponse response = workspaceService.getWorkspaceLicenseInfo(workspaceId);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @ApiOperation(value = "워크스페이스 정보 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 식별자", paramType = "path", example = "4d6eab0860969a50acbfa4599fbb5ae8")
    })
    @GetMapping("/{workspaceId}/info")
    public ResponseEntity<ApiResponse<WorkspaceInfoDTO>> getWorkspaceInfo(
            @PathVariable("workspaceId") String workspaceId
    ) {
        if (StringUtils.isEmpty(workspaceId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        WorkspaceInfoDTO response = workspaceService.getWorkspaceInfo(workspaceId);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @ApiOperation(value = "워크스페이스 관련 정보 삭제 - 회원탈퇴", tags = "user server only")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceUUID", value = "삭제할 워크스페이스의 식별자", paramType = "path", example = "4d6eab0860969a50acbfa4599fbb5ae8"),
            @ApiImplicitParam(name = "serviceID", value = "요청 서버 명", paramType = "header", example = "user-server")
    })
    @DeleteMapping("/secession/{workspaceUUID}")
    public ResponseEntity<ApiResponse<WorkspaceSecessionResponse>> workspaceSecessionRequest(
            @PathVariable("workspaceUUID") String workspaceUUID, @RequestHeader("serviceID") String requestServiceID
    ) {
        if (!StringUtils.hasText(workspaceUUID) || !StringUtils.hasText(requestServiceID) || !requestServiceID.equals(
                "user-server")) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        WorkspaceSecessionResponse responseMessage = workspaceService.deleteAllWorkspaceInfo(workspaceUUID);
        return ResponseEntity.ok(new ApiResponse<>(responseMessage));
    }
}
