package com.virnect.workspace.api;

import com.virnect.workspace.application.MemberService;
import com.virnect.workspace.dto.request.MemberKickOutRequest;
import com.virnect.workspace.dto.request.MemberUpdateRequest;
import com.virnect.workspace.dto.request.WorkspaceInviteRequest;
import com.virnect.workspace.dto.response.WorkspaceNewMemberInfoResponse;
import com.virnect.workspace.dto.response.WorkspaceUserInfoListResponse;
import com.virnect.workspace.dto.response.WorkspaceUserInfoResponse;
import com.virnect.workspace.dto.response.WorkspaceUserLicenseListResponse;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Project: PF-Workspace
 * DATE: 2021-02-02
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberController {
    private final MemberService memberService;

    @ApiOperation(
            value = "워크스페이스 멤버 검색(워크스페이스 멤버 목록 조회)",
            notes = "워크스페이스 멤버 검색으로 멤버를 조회합니다. \n 필터링 우선순위 : 검색어 > 필터(권한 또는 라이선스) > 페이징"
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
    public ResponseEntity<ApiResponse<WorkspaceUserInfoListResponse>> getMembers(
            @PathVariable("workspaceId") String workspaceId,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "filter", required = false) String filter, @ApiIgnore PageRequest pageable
    ) {
        if (!StringUtils.hasText(workspaceId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<WorkspaceUserInfoListResponse> apiResponse = memberService.getMembers(
                workspaceId, search, filter, pageable);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(
            value = "워크스페이스 신규 멤버 조회",
            notes = "워크스페이스 홈에서 워크스페이스의 신규 참여 멤버 정보를 조회합니다."
    )
    @GetMapping("/home/{workspaceId}/members")
    public ResponseEntity<ApiResponse<List<WorkspaceNewMemberInfoResponse>>> getWorkspaceNewUserInfo(
            @PathVariable("workspaceId") String workspaceId
    ) {
        if (!StringUtils.hasText(workspaceId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        List<WorkspaceNewMemberInfoResponse> response = memberService.getWorkspaceNewUserInfo(
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
        ApiResponse<Boolean> apiResponse = memberService.reviseMemberInfo(
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
    public ResponseEntity<ApiResponse<WorkspaceUserInfoResponse>> getMemberInfo(
            @PathVariable("workspaceId") String workspaceId, @RequestParam("userId") String userId
    ) {
        if (!StringUtils.hasText(workspaceId) || !StringUtils.hasText(userId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        WorkspaceUserInfoResponse response = memberService.getMemberInfo(workspaceId, userId);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }
    @ApiOperation(
            value = "사용자 식별자 배열로 워크스페이스 멤버 정보 조회",
            notes = "워크스페이스 내에서 해당 멤버의 정보를 조회합니다."
    )
    @GetMapping("/{workspaceId}/members/infoList")
    public ResponseEntity<ApiResponse<WorkspaceUserInfoListResponse>> getMemberInfoList(
            @PathVariable("workspaceId") String workspaceId, @RequestParam(value = "userIds") List<String> userIds
    ) {
        if (!StringUtils.hasText(workspaceId) || userIds==null || userIds.isEmpty()) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        WorkspaceUserInfoListResponse response = memberService.getMemberInfoList(workspaceId, userIds);
        return ResponseEntity.ok(new ApiResponse<>(response));
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
        ApiResponse<Boolean> apiResponse = memberService.kickOutMember(
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
        ApiResponse<Boolean> apiResponse = memberService.inviteWorkspace(
                workspaceId, workspaceInviteRequest, locale);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(
            value = "워크스페이스 멤버 초대 수락",
            notes = "초대받은 사용자가 이메일 인증에서 초대를 수락합니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sessionCode", value = "초대 세션 코드", paramType = "path", required = true),
            @ApiImplicitParam(name = "lang", value = "언어", paramType = "query", required = false)
    })
    @GetMapping("/invite/{sessionCode}/accept")
    public RedirectView inviteWorkspaceAccept(
            @PathVariable("sessionCode") String sessionCode,
            @RequestParam(value = "lang", required = false) String lang
    ) throws IOException {
        if (!StringUtils.hasText(sessionCode)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        RedirectView redirectView = memberService.inviteWorkspaceAccept(sessionCode, lang);
        return redirectView;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "sessionCode", value = "초대 세션 코드", paramType = "path", required = true),
            @ApiImplicitParam(name = "lang", value = "언어", paramType = "query", required = false)
    })
    @GetMapping("/invite/{sessionCode}/reject")
    public RedirectView inviteWorkspaceReject(
            @PathVariable("sessionCode") String sessionCode,
            @RequestParam(value = "lang", required = false) String lang
    ) {
        if (!StringUtils.hasText(sessionCode)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        RedirectView redirectView = memberService.inviteWorkspaceReject(sessionCode, lang);
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
    public ResponseEntity<ApiResponse<Boolean>> exitWorkspace(
            @PathVariable("workspaceId") String workspaceId, @RequestParam("userId") String userId, @ApiIgnore Locale locale
    ) {
        if (!StringUtils.hasText(workspaceId) || !StringUtils.hasText(userId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<Boolean> apiResponse = memberService.exitWorkspace(workspaceId, userId, locale);
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
    public ResponseEntity<ApiResponse<WorkspaceUserInfoListResponse>> getSimpleWorkspaceUserList(
            @PathVariable("workspaceId") String workspaceId
    ) {
        if (!StringUtils.hasText(workspaceId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        WorkspaceUserInfoListResponse response = memberService.getSimpleWorkspaceUserList(workspaceId);
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
        WorkspaceUserLicenseListResponse response = memberService.getLicenseWorkspaceUserList(workspaceId, pageRequest);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

}
