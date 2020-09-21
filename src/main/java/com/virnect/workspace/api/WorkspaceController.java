package com.virnect.workspace.api;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

import com.virnect.workspace.application.WorkspaceService;
import com.virnect.workspace.dto.UserInfoDTO;
import com.virnect.workspace.dto.WorkspaceInfoDTO;
import com.virnect.workspace.dto.WorkspaceNewMemberInfoDTO;
import com.virnect.workspace.dto.request.MemberKickOutRequest;
import com.virnect.workspace.dto.request.MemberUpdateRequest;
import com.virnect.workspace.dto.request.WorkspaceCreateRequest;
import com.virnect.workspace.dto.request.WorkspaceInviteRequest;
import com.virnect.workspace.dto.request.WorkspaceUpdateRequest;
import com.virnect.workspace.dto.response.MemberListResponse;
import com.virnect.workspace.dto.response.WorkspaceHistoryListResponse;
import com.virnect.workspace.dto.response.WorkspaceInfoListResponse;
import com.virnect.workspace.dto.response.WorkspaceInfoResponse;
import com.virnect.workspace.dto.response.WorkspaceLicenseInfoResponse;
import com.virnect.workspace.dto.response.WorkspaceSecessionResponse;
import com.virnect.workspace.dto.response.WorkspaceUserLicenseListResponse;
import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.common.ApiResponse;
import com.virnect.workspace.global.common.PageRequest;
import com.virnect.workspace.global.error.ErrorCode;

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
            value = "언어 설정"
    )
    @GetMapping("/locale")
    public void locale(@ApiIgnore Locale locale, @ApiParam(value = "언어", defaultValue = "ko") @RequestParam String lang) {
    }

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
    public ResponseEntity<ApiResponse<WorkspaceInfoDTO>> createWorkspace(@ModelAttribute @Valid WorkspaceCreateRequest workspaceCreateRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<WorkspaceInfoDTO> apiResponse = this.workspaceService.createWorkspace(workspaceCreateRequest);
        return ResponseEntity.ok(apiResponse);
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
    public ResponseEntity<ApiResponse<WorkspaceInfoDTO>> setWorkspace(@ModelAttribute @Valid WorkspaceUpdateRequest workspaceUpdateRequest, BindingResult bindingResult, @Validated @ApiIgnore Locale locale) {
        if (bindingResult.hasErrors()) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<WorkspaceInfoDTO> apiResponse = this.workspaceService.setWorkspace(workspaceUpdateRequest, locale);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(
            value = "워크스페이스 이미지 조회(개발 서버 업로드)"
    )
    @ApiImplicitParam(name = "fileName", value = "파일 이름", dataType = "string", type = "path", defaultValue = "1.PNG", required = true)
    @GetMapping("/virnect-platform/workspace/profile/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) throws IOException {
        if (!StringUtils.hasText(fileName)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        byte[] bytes = this.workspaceService.downloadFile(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(bytes.length)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(bytes);
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
    public ResponseEntity<ApiResponse<WorkspaceInfoListResponse>> getUserWorkspaces(@RequestParam("userId") String userId, @ApiIgnore PageRequest pageRequest) {
        if (!StringUtils.hasText(userId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<WorkspaceInfoListResponse> apiResponse = this.workspaceService.getUserWorkspaces(userId, pageRequest);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(
            value = "워크스페이스 멤버 검색(워크스페이스 멤버 목록 조회)",
            notes = "워크스페이스 멤버 검색으로 멤버를 조회합니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "search", value = "검색어(닉네임, 이메일)", dataType = "string", allowEmptyValue = true, defaultValue = ""),
            @ApiImplicitParam(name = "filter", value = "사용자 필터(MASTER, MANAGER, MEMBER)", dataType = "string", allowEmptyValue = true, defaultValue = ""),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "20"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "role,desc"),
    })
    @GetMapping("/{workspaceId}/members")
    public ResponseEntity<ApiResponse<MemberListResponse>> getMembers(@PathVariable("workspaceId") String workspaceId, @RequestParam(value = "search", required = false) String search, @RequestParam(value = "filter", required = false) String filter, @ApiIgnore PageRequest pageable) {
        if (!StringUtils.hasText(workspaceId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<MemberListResponse> apiResponse = this.workspaceService.getMembers(workspaceId, search, filter, pageable);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(
            value = "워크스페이스 상세 정보 조회",
            notes = "워크스페이스 홈에서 워크스페이스의 정보를 조회합니다."
    )
    @GetMapping("/home/{workspaceId}")
    public ResponseEntity<ApiResponse<WorkspaceInfoResponse>> getWorkspaceDetailInfo(@PathVariable("workspaceId") String workspaceId) {
        if (!StringUtils.hasText(workspaceId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<WorkspaceInfoResponse> apiResponse = this.workspaceService.getWorkspaceDetailInfo(workspaceId);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(
            value = "워크스페이스 신규 멤버 조회",
            notes = "워크스페이스 홈에서 워크스페이스의 신규 참여 멤버 정보를 조회합니다."
    )
    @GetMapping("/home/{workspaceId}/members")
    public ResponseEntity<ApiResponse<List<WorkspaceNewMemberInfoDTO>>> getWorkspaceNewUserInfo(@PathVariable("workspaceId") String workspaceId) {
        if (!StringUtils.hasText(workspaceId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<List<WorkspaceNewMemberInfoDTO>> apiResponse = this.workspaceService.getWorkspaceNewUserInfo(workspaceId);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(
            value = "워크스페이스 멤버 권한 설정",
            notes = "워크스페이스 내의 권한은 마스터 유저만 설정 가능하고 워크스페이스 내의 플랜은 마스터, 매니저유저만 가능합니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 uuid", dataType = "string", defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8", paramType = "path", required = true)
    })
    @PostMapping("/{workspaceId}/members/info")
    public ResponseEntity<ApiResponse<Boolean>> reviseUserPermission(@PathVariable("workspaceId") String workspaceId, @RequestBody @Valid MemberUpdateRequest memberUpdateRequest, BindingResult bindingResult, @ApiIgnore Locale locale) {
        if (!StringUtils.hasText(workspaceId) || bindingResult.hasErrors()) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<Boolean> apiResponse = this.workspaceService.reviseMemberInfo(workspaceId, memberUpdateRequest, locale);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(
            value = "워크스페이스 멤버 정보 조회",
            notes = "워크스페이스 내에서 해당 멤버의 정보를 조회합니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 uuid", dataType = "string", defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8", paramType = "path", required = true),
            @ApiImplicitParam(name = "userId", value = "유저 uuid", dataType = "string", defaultValue = "498b1839dc29ed7bb2ee90ad6985c608", paramType = "query", required = true)
    })
    @GetMapping("/{workspaceId}/members/info")
    public ResponseEntity<ApiResponse<UserInfoDTO>> getMemberInfo(@PathVariable("workspaceId") String workspaceId, @RequestParam("userId") String userId) {
        if (!StringUtils.hasText(workspaceId) || !StringUtils.hasText(userId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<UserInfoDTO> apiResponse = this.workspaceService.getMemberInfo(workspaceId, userId);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(
            value = "워크스페이스 멤버 내보내기",
            notes = "마스터 또는 매니저 유저가 워크스페이스 내에서 해당 멤버 또는 매니저를 내보내기합니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 uuid", dataType = "string", defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8", paramType = "path", required = true),
            @ApiImplicitParam(name = "userId", value = "요청 유저 uuid", dataType = "string", defaultValue = "498b1839dc29ed7bb2ee90ad6985c608", paramType = "form", required = true),
            @ApiImplicitParam(name = "kickedUserId", value = "내보내기 대상 유저 uuid", dataType = "string", defaultValue = "", paramType = "form", required = true)
    })
    @DeleteMapping("/{workspaceId}/members/info")
    public ResponseEntity<ApiResponse<Boolean>> kickOutMember(@PathVariable("workspaceId") String workspaceId, @ModelAttribute @Valid MemberKickOutRequest memberKickOutRequest, BindingResult bindingResult, @ApiIgnore Locale locale) {
        if (!StringUtils.hasText(workspaceId) || bindingResult.hasErrors()) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<Boolean> apiResponse = this.workspaceService.kickOutMember(workspaceId, memberKickOutRequest, locale);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(
            value = "워크스페이스 멤버 초대하기",
            notes = "워크스페이스 내에서 사용자를 초대합니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 uuid", dataType = "string", defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8", paramType = "path", required = true)
    })
    @PostMapping("/{workspaceId}/invite")
    public ResponseEntity<ApiResponse<Boolean>> inviteWorkspace(@PathVariable("workspaceId") String workspaceId, @RequestBody @Valid WorkspaceInviteRequest workspaceInviteRequest, @ApiIgnore Locale locale, BindingResult bindingResult) {
        if (!StringUtils.hasText(workspaceId) || bindingResult.hasErrors()) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<Boolean> apiResponse = this.workspaceService.inviteWorkspace(workspaceId, workspaceInviteRequest, locale);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(
            value = "워크스페이스 멤버 초대 수락",
            notes = "초대받은 사용자가 이메일 인증에서 초대를 수락합니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 uuid", dataType = "string", defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8", paramType = "path", required = true),
            @ApiImplicitParam(name = "userId", value = "초대받은 사용자 uuid", dataType = "string", defaultValue = "498b1839dc29ed7bb2ee90ad6985c60", paramType = "query", required = true),
            @ApiImplicitParam(name = "accept", value = "초대 수락 또는 거절 선택값", paramType = "query", required = true),
            @ApiImplicitParam(name = "lang", value = "언어", paramType = "query", required = true)
    })
    @GetMapping("/{workspaceId}/invite/accept")
    public RedirectView inviteWorkspaceAccept(@PathVariable("workspaceId") String workspaceId, @RequestParam("userId") String userId,
                                              @RequestParam("accept") Boolean accept,
        @RequestParam("lang") String lang) {
        if (!StringUtils.hasText(workspaceId) || !StringUtils.hasText(userId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        RedirectView redirectView = this.workspaceService.inviteWorkspaceResult(workspaceId, userId, accept, lang);
        return redirectView;
    }

    @ApiOperation(
            value = "워크스페이스 나가기",
            notes = "본인이 매니저 또는 멤버로 소속되어 있는 워크스페이스에서 나갑니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 uuid", dataType = "string", defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8", paramType = "path", required = true),
            @ApiImplicitParam(name = "userId", value = "유저 uuid", dataType = "string", defaultValue = "498b1839dc29ed7bb2ee90ad6985c608", paramType = "query", required = true),
    })
    @DeleteMapping("/{workspaceId}/exit")
    public ResponseEntity<ApiResponse<Boolean>> exitWorkspace(@PathVariable("workspaceId") String workspaceId, @RequestParam("userId") String userId, @ApiIgnore Locale locale) {
        if (!StringUtils.hasText(workspaceId) || !StringUtils.hasText(userId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<Boolean> apiResponse = this.workspaceService.exitWorkspace(workspaceId, userId, locale);
        return ResponseEntity.ok(apiResponse);
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
    public ResponseEntity<ApiResponse<WorkspaceHistoryListResponse>> getWorkspaceHistory(@PathVariable("workspaceId") String workspaceId, @RequestParam("userId") String userId, @ApiIgnore PageRequest pageRequest) {
        if (!StringUtils.hasText(workspaceId) || !StringUtils.hasText(userId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<WorkspaceHistoryListResponse> apiResponse = this.workspaceService.getWorkspaceHistory(workspaceId, userId, pageRequest.of());
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(
            value = "워크스페이스 멤버 조회",
            notes = "워크스페이스 내의 마스터, 매니저를 포함한 모든 멤버를 조회합니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 uuid", dataType = "string", defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8", paramType = "path", required = true),
    })
    @GetMapping("/{workspaceId}/members/simple")
    public ResponseEntity<ApiResponse<MemberListResponse>> getSimpleWorkspaceUserList(@PathVariable("workspaceId") String workspaceId) {
        if (!StringUtils.hasText(workspaceId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<MemberListResponse> apiResponse = this.workspaceService.getSimpleWorkspaceUserList(workspaceId);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(
            value = "워크스페이스 플랜 사용자 조회",
            notes = "워크스페이스 내의 사용자를 플랜을 기준으로 조회합니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 uuid", dataType = "string", defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8", paramType = "path", required = true),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "20"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "plan,desc")
    })
    @GetMapping("/{workspaceId}/members/license")
    public ResponseEntity<ApiResponse<WorkspaceUserLicenseListResponse>> getWorkspaceUserLicenseList(@PathVariable("workspaceId") String workspaceId, @ApiIgnore PageRequest pageRequest) {
        if (!StringUtils.hasText(workspaceId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<WorkspaceUserLicenseListResponse> apiResponse = this.workspaceService.getLicenseWorkspaceUserList(workspaceId, pageRequest.of());
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
    public ResponseEntity<ApiResponse<WorkspaceLicenseInfoResponse>> getWorkspaceLicenseInfo(@PathVariable("workspaceId") String workspaceId) {
        if (!StringUtils.hasText(workspaceId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<WorkspaceLicenseInfoResponse> apiResponse = this.workspaceService.getWorkspaceLicenseInfo(workspaceId);
        return ResponseEntity.ok(apiResponse);
    }
/*
    @ApiOperation(
            value = "(테스트용)워크스페이스 멤버 추가",
            notes = "개발서버에서 테스트 데이터 넣기 위함."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 uuid", dataType = "string", defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8", paramType = "path", required = true)
    })
    @PostMapping("/{workspaceId}/test")
    public ResponseEntity<ApiResponse<Boolean>> testSetMember(@PathVariable("workspaceId") String workspaceId, @RequestBody @Valid WorkspaceInviteRequest workspaceInviteRequest, BindingResult bindingResult) {
        if (!StringUtils.hasText(workspaceId) || bindingResult.hasErrors()) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<Boolean> apiResponse = this.workspaceService.testSetMember(workspaceId, workspaceInviteRequest);
        return ResponseEntity.ok(apiResponse);
    }
*/

    @ApiOperation(value = "워크스페이스 정보 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 식별자", paramType = "path", example = "4d6eab0860969a50acbfa4599fbb5ae8")
    })
    @GetMapping("/{workspaceId}/info")
    public ResponseEntity<ApiResponse<WorkspaceInfoDTO>> getWorkspaceInfo(@PathVariable("workspaceId") String workspaceId) {
        if (StringUtils.isEmpty(workspaceId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<WorkspaceInfoDTO> responseMessage = workspaceService.getWorkspaceInfo(workspaceId);
        return ResponseEntity.ok(responseMessage);
    }

	@ApiOperation(value = "워크스페이스 관련 정보 삭제 - 회원탈퇴", tags = "user server only")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceUUID", value = "삭제할 워크스페이스의 식별자", paramType = "path", example = "4d6eab0860969a50acbfa4599fbb5ae8"),
		@ApiImplicitParam(name = "serviceID", value = "요청 서버 명", paramType = "header", example = "user-server")
	})
	@DeleteMapping("/secession/{workspaceUUID}")
	public ResponseEntity<ApiResponse<WorkspaceSecessionResponse>> workspaceSecessionRequest(@PathVariable("workspaceUUID") String workspaceUUID, @RequestHeader("serviceID") String requestServiceID) {
		if (!StringUtils.hasText(workspaceUUID) || !StringUtils.hasText(requestServiceID) || !requestServiceID.equals("user-server")) {
			throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		WorkspaceSecessionResponse responseMessage = workspaceService.deleteAllWorkspaceInfo(workspaceUUID);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}
}
