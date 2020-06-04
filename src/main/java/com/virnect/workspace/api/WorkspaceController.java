package com.virnect.workspace.api;

import com.virnect.workspace.application.WorkspaceService;
import com.virnect.workspace.dto.UserInfoDTO;
import com.virnect.workspace.dto.WorkspaceInfoDTO;
import com.virnect.workspace.dto.WorkspaceNewMemberInfoDTO;
import com.virnect.workspace.dto.request.*;
import com.virnect.workspace.dto.response.MemberListResponse;
import com.virnect.workspace.dto.response.WorkspaceHistoryListResponse;
import com.virnect.workspace.dto.response.WorkspaceInfoListResponse;
import com.virnect.workspace.dto.response.WorkspaceInfoResponse;
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
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    public ResponseEntity<ApiResponse<WorkspaceInfoDTO>> setWorkspace(@ModelAttribute @Valid WorkspaceUpdateRequest workspaceUpdateRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<WorkspaceInfoDTO> apiResponse = this.workspaceService.setWorkspace(workspaceUpdateRequest);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(
            value = "워크스페이스 이미지 조회(개발 서버 업로드)"
    )
    @ApiImplicitParam(name = "fileName", value = "파일 이름", dataType = "string", type = "path", defaultValue = "1.PNG", required = true)
    @GetMapping("/upload/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws IOException {
        if (!StringUtils.hasText(fileName)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        Resource resource = this.workspaceService.downloadFile(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
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
        ApiResponse<WorkspaceInfoListResponse> apiResponse = this.workspaceService.getUserWorkspaces(userId, pageRequest.of());
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
            value = "워크스페이스 정보 조회",
            notes = "워크스페이스 홈에서 워크스페이스의 정보를 조회합니다."
    )
    @GetMapping("/home/{workspaceId}")
    public ResponseEntity<ApiResponse<WorkspaceInfoResponse>> getWorkspaceInfo(@PathVariable("workspaceId") String workspaceId) {
        if (!StringUtils.hasText(workspaceId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<WorkspaceInfoResponse> apiResponse = this.workspaceService.getWorkspaceInfo(workspaceId);
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
            notes = "마스터 또는 매니저 유저가 워크스페이스 내에서 해당 멤버를 내보내기합니다."
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
    public ResponseEntity<ApiResponse<Boolean>> inviteWorkspace(@PathVariable("workspaceId") String workspaceId, @RequestBody @Valid WorkspaceInviteRequest workspaceInviteRequest, BindingResult bindingResult) {
        if (!StringUtils.hasText(workspaceId) || bindingResult.hasErrors()) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<Boolean> apiResponse = this.workspaceService.inviteWorkspace(workspaceId, workspaceInviteRequest);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(
            value = "워크스페이스 멤버 초대 수락",
            notes = "초대받은 사용자가 이메일 인증에서 초대를 수락합니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 uuid", dataType = "string", defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8", paramType = "path", required = true),
            @ApiImplicitParam(name = "userId", value = "초대받은 사용자 uuid", dataType = "string", defaultValue = "498b1839dc29ed7bb2ee90ad6985c60", paramType = "query", required = true)
    })
    @GetMapping("/{workspaceId}/invite/accept")
    public RedirectView inviteWorkspaceAccept(@PathVariable("workspaceId") String workspaceId, @RequestParam("userId") String userId,
                                              @ApiIgnore Locale locale) {
        if (!StringUtils.hasText(workspaceId) || !StringUtils.hasText(userId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        RedirectView redirectView = this.workspaceService.inviteWorkspaceAccept(workspaceId, userId, locale);
        return redirectView;
    }

    @ApiOperation(
            value = "워크스페이스 멤버 초대 거절",
            notes = "초대받은 사용자가 이메일 인증에서 초대를 거절합니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 uuid", dataType = "string", defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8", paramType = "path", required = true),
            @ApiImplicitParam(name = "userId", value = "초대받은 사용자 uuid", dataType = "string", defaultValue = "498b1839dc29ed7bb2ee90ad6985c60", paramType = "query", required = true)
    })
    @GetMapping("/{workspaceId}/invite/reject")
    public RedirectView inviteWorkspaceReject(@PathVariable("workspaceId") String workspaceId, @RequestParam("userId") String userId,
                                              @ApiIgnore Locale locale) {
        if (!StringUtils.hasText(workspaceId) || !StringUtils.hasText(userId)) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        RedirectView redirectView = this.workspaceService.inviteWorkspaceReject(workspaceId, userId, locale);
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

}
