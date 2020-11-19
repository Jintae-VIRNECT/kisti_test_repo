package com.virnect.workspace.api;

import com.virnect.workspace.application.WorkspaceService;
import com.virnect.workspace.dto.UserInfoDTO;
import com.virnect.workspace.dto.WorkspaceInfoDTO;
import com.virnect.workspace.dto.WorkspaceNewMemberInfoDTO;
import com.virnect.workspace.dto.onpremise.*;
import com.virnect.workspace.dto.request.*;
import com.virnect.workspace.dto.response.*;
import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.common.ApiResponse;
import com.virnect.workspace.global.common.PageRequest;
import com.virnect.workspace.global.error.ErrorCode;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
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
            value = "언어 설정"
    )
    @GetMapping("/locale")
    public void locale(
            @ApiIgnore Locale locale, @ApiParam(value = "언어", defaultValue = "ko") @RequestParam String lang
    ) {
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
            value = "워크스페이스 멤버 검색(워크스페이스 멤버 목록 조회)",
            notes = "워크스페이스 멤버 검색으로 멤버를 조회합니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 식별자", defaultValue = "45ea004001c56a3380d48168b9db0492", required = true),
            @ApiImplicitParam(name = "search", value = "검색어(닉네임, 이메일)", dataType = "string", allowEmptyValue = true, defaultValue = ""),
            @ApiImplicitParam(name = "filter", value = "사용자 필터(MASTER, MANAGER, MEMBER) 또는 (REMOTE, MAKE, VIEW)", dataType = "string", allowEmptyValue = true, defaultValue = ""),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "20"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(role, joinDate, email, nickname)", paramType = "query", defaultValue = "role,desc"),
    })
    @GetMapping("/{workspaceId}/members")
    public ResponseEntity<ApiResponse<MemberListResponse>> getMembers(
            @PathVariable("workspaceId") String workspaceId,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "filter", required = false) String filter, @ApiIgnore PageRequest pageable
    ) {
        if (!StringUtils.hasText(workspaceId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<MemberListResponse> apiResponse = workspaceService.getMembers(
                workspaceId, search, filter, pageable);
        return ResponseEntity.ok(apiResponse);
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
            value = "워크스페이스 신규 멤버 조회",
            notes = "워크스페이스 홈에서 워크스페이스의 신규 참여 멤버 정보를 조회합니다."
    )
    @GetMapping("/home/{workspaceId}/members")
    public ResponseEntity<ApiResponse<List<WorkspaceNewMemberInfoDTO>>> getWorkspaceNewUserInfo(
            @PathVariable("workspaceId") String workspaceId
    ) {
        if (!StringUtils.hasText(workspaceId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        List<WorkspaceNewMemberInfoDTO> response = workspaceService.getWorkspaceNewUserInfo(
                workspaceId);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @ApiOperation(
            value = "워크스페이스 멤버 권한 설정",
            notes = "워크스페이스 내의 권한은 마스터 유저만 설정 가능하고 워크스페이스 내의 플랜은 마스터, 매니저유저만 가능합니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 uuid", dataType = "string", defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8", paramType = "path", required = true)
    })
    @PostMapping("/{workspaceId}/members/info")
    public ResponseEntity<ApiResponse<Boolean>> reviseUserPermission(
            @PathVariable("workspaceId") String workspaceId, @RequestBody @Valid MemberUpdateRequest memberUpdateRequest,
            BindingResult bindingResult, @ApiIgnore Locale locale
    ) {
        if (!StringUtils.hasText(workspaceId) || bindingResult.hasErrors()) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<Boolean> apiResponse = workspaceService.reviseMemberInfo(
                workspaceId, memberUpdateRequest, locale);
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
    public ResponseEntity<ApiResponse<UserInfoDTO>> getMemberInfo(
            @PathVariable("workspaceId") String workspaceId, @RequestParam("userId") String userId
    ) {
        if (!StringUtils.hasText(workspaceId) || !StringUtils.hasText(userId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        UserInfoDTO responese = workspaceService.getMemberInfo(workspaceId, userId);
        return ResponseEntity.ok(new ApiResponse<>(responese));
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
    public ResponseEntity<ApiResponse<Boolean>> kickOutMember(
            @PathVariable("workspaceId") String workspaceId,
            @ModelAttribute @Valid MemberKickOutRequest memberKickOutRequest, BindingResult bindingResult,
            @ApiIgnore Locale locale
    ) {
        if (!StringUtils.hasText(workspaceId) || bindingResult.hasErrors()) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<Boolean> apiResponse = workspaceService.kickOutMember(
                workspaceId, memberKickOutRequest, locale);
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
    public ResponseEntity<ApiResponse<Boolean>> inviteWorkspace(
            @PathVariable("workspaceId") String workspaceId,
            @RequestBody @Valid WorkspaceInviteRequest workspaceInviteRequest,
            @ApiIgnore Locale locale,
            BindingResult bindingResult
    ) {
        if (!StringUtils.hasText(workspaceId) || bindingResult.hasErrors()) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<Boolean> apiResponse = workspaceService.inviteWorkspace(
                workspaceId, workspaceInviteRequest, locale);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(
            value = "워크스페이스 멤버 초대 수락",
            notes = "초대받은 사용자가 이메일 인증에서 초대를 수락합니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sessionCode", value = "초대 세션 코드", paramType = "path", required = true),
            @ApiImplicitParam(name = "lang", value = "언어", paramType = "query", required = true)
    })
    @GetMapping("/invite/{sessionCode}/accept")
    public void inviteWorkspaceAccept(
            @PathVariable("sessionCode") String sessionCode,
            @RequestParam("lang") String lang,
            @ApiIgnore HttpServletResponse httpServletResponse
    ) throws IOException {
        if (!StringUtils.hasText(sessionCode)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        workspaceService.inviteWorkspaceAccept(sessionCode, lang, httpServletResponse);
        /*RedirectView redirectView =workspaceService.inviteWorkspaceAccept(sessionCode, lang, httpServletResponse);
        return redirectView;*/
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "sessionCode", value = "초대 세션 코드", paramType = "path", required = true),
            @ApiImplicitParam(name = "lang", value = "언어", paramType = "query", required = true)
    })
    @GetMapping("/invite/{sessionCode}/reject")
    public RedirectView inviteWorkspaceReject(
            @PathVariable("sessionCode") String sessionCode,
            @RequestParam("lang") String lang
    ) {
        if (!StringUtils.hasText(sessionCode)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        RedirectView redirectView = workspaceService.inviteWorkspaceReject(sessionCode, lang);
        return redirectView;
    }
/*
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
	public RedirectView inviteWorkspaceAccept(
		@PathVariable("workspaceId") String workspaceId, @RequestParam("userId") String userId,
		@RequestParam("accept") Boolean accept,
		@RequestParam("lang") String lang
	) {
		if (!StringUtils.hasText(workspaceId) || !StringUtils.hasText(userId)) {
			throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		RedirectView redirectView = workspaceService.inviteWorkspaceResult(workspaceId, userId, accept, lang);
		return redirectView;
	}*/

    @ApiOperation(
            value = "워크스페이스 나가기",
            notes = "본인이 매니저 또는 멤버로 소속되어 있는 워크스페이스에서 나갑니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 uuid", dataType = "string", defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8", paramType = "path", required = true),
            @ApiImplicitParam(name = "userId", value = "유저 uuid", dataType = "string", defaultValue = "498b1839dc29ed7bb2ee90ad6985c608", paramType = "query", required = true),
    })
    @DeleteMapping("/{workspaceId}/exit")
    public ResponseEntity<ApiResponse<Boolean>> exitWorkspace(
            @PathVariable("workspaceId") String workspaceId, @RequestParam("userId") String userId, @ApiIgnore Locale locale
    ) {
        if (!StringUtils.hasText(workspaceId) || !StringUtils.hasText(userId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<Boolean> apiResponse = workspaceService.exitWorkspace(workspaceId, userId, locale);
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
            value = "워크스페이스 멤버 조회",
            notes = "워크스페이스 내의 마스터, 매니저를 포함한 모든 멤버를 조회합니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 uuid", dataType = "string", defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8", paramType = "path", required = true),
    })
    @GetMapping("/{workspaceId}/members/simple")
    public ResponseEntity<ApiResponse<MemberListResponse>> getSimpleWorkspaceUserList(
            @PathVariable("workspaceId") String workspaceId
    ) {
        if (!StringUtils.hasText(workspaceId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        MemberListResponse response = workspaceService.getSimpleWorkspaceUserList(workspaceId);
        return ResponseEntity.ok(new ApiResponse<>(response));
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
    public ResponseEntity<ApiResponse<WorkspaceUserLicenseListResponse>> getWorkspaceUserLicenseList(
            @PathVariable("workspaceId") String workspaceId, @ApiIgnore PageRequest pageRequest
    ) {
        if (!StringUtils.hasText(workspaceId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        WorkspaceUserLicenseListResponse response = workspaceService.getLicenseWorkspaceUserList(workspaceId, pageRequest.of());
        return ResponseEntity.ok(new ApiResponse<>(response));
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
        ApiResponse<Boolean> apiResponse = workspaceService.testSetMember(workspaceId, workspaceInviteRequest);
        return ResponseEntity.ok(apiResponse);
    }
*/

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

    @Profile("onpremise")
    @ApiOperation(value = "워크스페이스 멤버 계정 생성", tags = "onpremise server only")
    @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 식별자", dataType = "string", defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8", paramType = "path", required = true)
    @PostMapping("/{workspaceId}/members/account")
    public ResponseEntity<ApiResponse<WorkspaceMemberInfoListResponse>> createWorkspaceMemberAccount(
            @PathVariable("workspaceId") String workspaceId,
            @RequestBody @Valid MemberAccountCreateRequest memberAccountCreateRequest, BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors()
                    .forEach(
                            objectError -> log.error("[CREATE WORKSPACE MEMBER ACCOUNT] Error message : [{}]", objectError));
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        WorkspaceMemberInfoListResponse response = workspaceService.createWorkspaceMemberAccount(
                workspaceId,
                memberAccountCreateRequest
        );
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @Profile("onpremise")
    @ApiOperation(value = "워크스페이스 멤버 계정 삭제 및 내보내기", tags = "onpremise server only")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 식별자", dataType = "string", defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8", paramType = "path", required = true)
    })
    @DeleteMapping("/{workspaceId}/members/account")
    public ResponseEntity<ApiResponse<Boolean>> removeWorkspaceMemberAccount(
            @PathVariable("workspaceId") String workspaceId,
            @RequestBody @Valid MemberAccountDeleteRequest memberAccountDeleteRequest, BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors()
                    .forEach(
                            objectError -> log.error(
                                    "[DELETE WORKSPACE MEMBER ACCOUNT] Parameter Error message : [{}]", objectError));
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        Boolean response = workspaceService.deleteWorkspaceMemberAccount(workspaceId, memberAccountDeleteRequest);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @Profile("onpremise")
    @ApiOperation(value = "워크스페이스 멤버 비밀번호 재설정", tags = "onpremise server only")
    @PostMapping("/{workspaceId}/members/password")
    public ResponseEntity<ApiResponse<WorkspaceMemberPasswordChangeResponse>> memberPasswordChangeRequest(
            @PathVariable("workspaceId") String workspaceId,
            @RequestBody WorkspaceMemberPasswordChangeRequest passwordChangeRequest,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(
                    objectError -> log.error("[WORKSPACE MEMBER PASSWORD CHANGE] Error message : [{}]", objectError)
            );
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        WorkspaceMemberPasswordChangeResponse response = workspaceService.memberPasswordChange(passwordChangeRequest, workspaceId);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @Profile("onpremise")
    @ApiOperation(value = "워크스페이스 고객사명 변경", tags = "onpremise server only")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 식별자", dataType = "string", defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8", paramType = "path", required = true),
    })
    @PostMapping("/{workspaceId}/title")
    public ResponseEntity<ApiResponse<WorkspaceTitleUpdateResponse>> updateWorkspaceTitle(
            @PathVariable("workspaceId") String workspaceId,
            @RequestBody @Valid WorkspaceTitleUpdateRequest workspaceTitleUpdateRequest, BindingResult bindingResult
    ) {
        if (!StringUtils.hasText(workspaceId) || bindingResult.hasErrors()) {
            bindingResult.getAllErrors()
                    .forEach(
                            objectError -> log.error("[UPDATE WORKSPACE TITLE] Parameter Error Message : [{}]", objectError));
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        WorkspaceTitleUpdateResponse workspaceTitleUpdateResponse = workspaceService.updateWorkspaceTitle(
                workspaceId, workspaceTitleUpdateRequest);
        return ResponseEntity.ok(new ApiResponse<>(workspaceTitleUpdateResponse));

    }

    @Profile("onpremise")
    @ApiOperation(value = "워크스페이스 로고 변경", tags = "onpremise server only")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 식별자", dataType = "string", defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8", paramType = "path", required = true),
            @ApiImplicitParam(name = "userId", value = "로고 변경 유저 식별자", dataType = "string", paramType = "form", required = true, example = "498b1839dc29ed7bb2ee90ad6985c608"),
            @ApiImplicitParam(name = "defaultLogo", value = "로고 이미지(생략 시 기본이미지)", dataType = "__file", paramType = "form", required = false),
            @ApiImplicitParam(name = "greyLogo", value = "로고 그레이 이미지", dataType = "__file", paramType = "form", required = false),
            @ApiImplicitParam(name = "whiteLogo", value = "로고 화이트 이미지", dataType = "__file", paramType = "form", required = false),
    })
    @PostMapping("/{workspaceId}/logo")
    public ResponseEntity<ApiResponse<WorkspaceLogoUpdateResponse>> updateWorkspaceLogo(
            @PathVariable("workspaceId") String workspaceId,
            @ModelAttribute @Valid WorkspaceLogoUpdateRequest workspaceLogoUpdateRequest, BindingResult bindingResult
    ) {
        if (!StringUtils.hasText(workspaceId) || bindingResult.hasErrors()) {
            bindingResult.getAllErrors()
                    .forEach(
                            objectError -> log.error("[UPDATE WORKSPACE LOGO] Parameter Error Message : [{}]", objectError));
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        WorkspaceLogoUpdateResponse workspaceLogoUpdateResponse = workspaceService.updateWorkspaceLogo(
                workspaceId, workspaceLogoUpdateRequest);
        return ResponseEntity.ok(new ApiResponse<>(workspaceLogoUpdateResponse));

    }

    @Profile("onpremise")
    @ApiOperation(value = "워크스페이스 파비콘 변경", tags = "onpremise server only")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 식별자", dataType = "string", defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8", paramType = "path", required = true),
            @ApiImplicitParam(name = "userId", value = "파비콘 변경 유저 식별자", dataType = "string", paramType = "form", required = true, example = "498b1839dc29ed7bb2ee90ad6985c608"),
            @ApiImplicitParam(name = "favicon", value = "파비콘 이미지(생략 시 기본이미지)", dataType = "__file", paramType = "form", required = false)
    })
    @PostMapping("/{workspaceId}/favicon")
    public ResponseEntity<ApiResponse<WorkspaceFaviconUpdateResponse>> updateWorkspaceFavicon(
            @PathVariable("workspaceId") String workspaceId,
            @ModelAttribute WorkspaceFaviconUpdateRequest workspaceFaviconUpdateRequest
    ) {
        if (!StringUtils.hasText(workspaceId) || !StringUtils.hasText(workspaceFaviconUpdateRequest.getUserId())) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        WorkspaceFaviconUpdateResponse workspaceFaviconUpdateResponse = workspaceService.updateWorkspaceFavicon(
                workspaceId, workspaceFaviconUpdateRequest);
        return ResponseEntity.ok(new ApiResponse<>(workspaceFaviconUpdateResponse));
    }

    @Profile("onpremise")
    @ApiOperation(value = "워크스페이스 커스텀 설정 조회", tags = "onpremise server only")
    @GetMapping("/setting")
    public ResponseEntity<ApiResponse<WorkspaceCustomSettingResponse>> getWorkspaceCustomSetting(
    ) {
        WorkspaceCustomSettingResponse workspaceCustomSettingResponse = workspaceService.getWorkspaceCustomSetting();
        return ResponseEntity.ok(new ApiResponse<>(workspaceCustomSettingResponse));
    }

    @ApiOperation(value = "전체 워크스페이스 멤버 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "20"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "updatedDate,desc")
    })
    @GetMapping("/members")
    public ResponseEntity<ApiResponse<WorkspaceInfoListResponse>> getAllWorkspaceUserList(@ApiIgnore PageRequest pageRequest
    ) {
        WorkspaceInfoListResponse workspaceUserInfoListResponse = workspaceService.getAllWorkspaceUserList(pageRequest.of());
        return ResponseEntity.ok(new ApiResponse<>(workspaceUserInfoListResponse));
    }
}
