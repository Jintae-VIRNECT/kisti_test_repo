package com.virnect.workspace.api;

import com.virnect.workspace.application.OnpremiseService;
import com.virnect.workspace.dto.onpremise.*;
import com.virnect.workspace.dto.request.MemberAccountDeleteRequest;
import com.virnect.workspace.dto.request.WorkspaceMemberPasswordChangeRequest;
import com.virnect.workspace.dto.response.WorkspaceMemberInfoListResponse;
import com.virnect.workspace.dto.response.WorkspaceMemberPasswordChangeResponse;
import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.common.ApiResponse;
import com.virnect.workspace.global.error.ErrorCode;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
public class OnpremiseController {
    private final OnpremiseService onpremiseService;

    @Profile("onpremise")
    @ApiOperation(value = "워크스페이스 멤버 계정 생성")
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
        WorkspaceMemberInfoListResponse response = onpremiseService.createWorkspaceMemberAccount(
                workspaceId,
                memberAccountCreateRequest
        );
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @Profile("onpremise")
    @ApiOperation(value = "워크스페이스 멤버 계정 삭제 및 내보내기")
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
        Boolean response = onpremiseService.deleteWorkspaceMemberAccount(workspaceId, memberAccountDeleteRequest);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @Profile("onpremise")
    @ApiOperation(value = "워크스페이스 멤버 비밀번호 재설정")
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
        WorkspaceMemberPasswordChangeResponse response = onpremiseService.memberPasswordChange(passwordChangeRequest, workspaceId);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @Profile("onpremise")
    @ApiOperation(value = "워크스페이스 고객사명 변경")
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
        WorkspaceTitleUpdateResponse workspaceTitleUpdateResponse = onpremiseService.updateWorkspaceTitle(
                workspaceId, workspaceTitleUpdateRequest);
        return ResponseEntity.ok(new ApiResponse<>(workspaceTitleUpdateResponse));

    }

    @Profile("onpremise")
    @ApiOperation(value = "워크스페이스 로고 변경")
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
        WorkspaceLogoUpdateResponse workspaceLogoUpdateResponse = onpremiseService.updateWorkspaceLogo(
                workspaceId, workspaceLogoUpdateRequest);
        return ResponseEntity.ok(new ApiResponse<>(workspaceLogoUpdateResponse));

    }

    @Profile("onpremise")
    @ApiOperation(value = "워크스페이스 파비콘 변경")
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
        WorkspaceFaviconUpdateResponse workspaceFaviconUpdateResponse = onpremiseService.updateWorkspaceFavicon(
                workspaceId, workspaceFaviconUpdateRequest);
        return ResponseEntity.ok(new ApiResponse<>(workspaceFaviconUpdateResponse));
    }

    @Profile("onpremise")
    @ApiOperation(value = "워크스페이스 커스텀 설정 조회")
    @GetMapping("/setting")
    public ResponseEntity<ApiResponse<WorkspaceCustomSettingResponse>> getWorkspaceCustomSetting(
    ) {
        WorkspaceCustomSettingResponse workspaceCustomSettingResponse = onpremiseService.getWorkspaceCustomSetting();
        return ResponseEntity.ok(new ApiResponse<>(workspaceCustomSettingResponse));
    }
}
