package com.virnect.workspace.api;

import com.virnect.workspace.application.workspace.WorkspaceService;
import com.virnect.workspace.domain.setting.Product;
import com.virnect.workspace.dto.WorkspaceInfoDTO;
import com.virnect.workspace.dto.onpremise.*;
import com.virnect.workspace.dto.request.SettingUpdateRequest;
import com.virnect.workspace.dto.request.WorkspaceCreateRequest;
import com.virnect.workspace.dto.request.WorkspaceSettingUpdateRequest;
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
import org.springframework.context.annotation.Profile;
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
            @ApiImplicitParam(name = "userUUID", value = "유저의 식별자", paramType = "path", example = "LtvGcPoq0WUFv"),
            @ApiImplicitParam(name = "serviceID", value = "요청 서버 명", paramType = "header", example = "user-server")
    })
    @DeleteMapping("/secession/{userUUID}")
    public ResponseEntity<ApiResponse<WorkspaceSecessionResponse>> workspaceSecessionRequest(
            @PathVariable("userUUID") String userUUID, @RequestHeader("serviceID") String requestServiceID
    ) {
        if (!StringUtils.hasText(userUUID) || !StringUtils.hasText(requestServiceID) || !requestServiceID.equals(
                "user-server")) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        WorkspaceSecessionResponse responseMessage = workspaceService.deleteAllWorkspaceInfo(userUUID);
        return ResponseEntity.ok(new ApiResponse<>(responseMessage));
    }

    @Profile("onpremise")
    @ApiOperation(value = "워크스페이스 고객사명 변경", tags = "on-premise only")
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
    @ApiOperation(value = "워크스페이스 로고 변경", tags = "on-premise only")
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
    @ApiOperation(value = "워크스페이스 파비콘 변경", tags = "on-premise only")
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
    @ApiOperation(value = "워크스페이스 커스텀 설정 조회", tags = "on-premise only")
    @GetMapping("/setting")
    public ResponseEntity<ApiResponse<WorkspaceCustomSettingResponse>> getWorkspaceCustomSetting(
    ) {
        WorkspaceCustomSettingResponse workspaceCustomSettingResponse = workspaceService.getWorkspaceCustomSetting();
        return ResponseEntity.ok(new ApiResponse<>(workspaceCustomSettingResponse));
    }

    @ApiOperation(value = "워크스페이스 설정 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 식별자", defaultValue = "4129d4ed6dd545418b4c5562ae94b27b", required = true),
            @ApiImplicitParam(name = "product", value = "설정 제품 이름", required = true, example = "WORKSTATION"),
    })
    @GetMapping("/{workspaceId}/settings")
    public ResponseEntity<ApiResponse<WorkspaceSettingInfoListResponse>> findWorkspaceSettingList(@PathVariable("workspaceId") String workspaceId, @RequestParam("product") Product product) {
        if (!StringUtils.hasText(workspaceId) || product == null) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        WorkspaceSettingInfoListResponse responseMessage = workspaceService.getWorkspaceSettingList(workspaceId, product);
        return ResponseEntity.ok(new ApiResponse<>(responseMessage));
    }

    @ApiOperation(value = "워크스페이스 설정")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 식별자", defaultValue = "4129d4ed6dd545418b4c5562ae94b27b", required = true),
            @ApiImplicitParam(name = "userId", value = "유저 식별자", defaultValue = "4163b24f04b699efb817fa2df192456a", required = true),
    })
    @PostMapping("/{workspaceId}/{userId}/settings")
    public ResponseEntity<ApiResponse<WorkspaceSettingUpdateResponse>> updateWorkspaceSetting(@PathVariable("workspaceId") String workspaceId,
                                                                                              @PathVariable("userId") String userId,
                                                                                              @RequestBody WorkspaceSettingUpdateRequest workspaceSettingUpdateRequest
            , BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors() || !StringUtils.hasText(workspaceId) || !StringUtils.hasText(userId)) {
            bindingResult.getAllErrors()
                    .forEach(
                            objectError -> log.error("Parameter Error Message : [{}]", objectError));
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        WorkspaceSettingUpdateResponse responseMessage = workspaceService.updateWorkspaceSetting(workspaceId, userId, workspaceSettingUpdateRequest);
        return ResponseEntity.ok(new ApiResponse<>(responseMessage));
    }

    @ApiOperation(value = "설정 정보 목록 조회", tags = "only license server")
    @GetMapping("/settings")
    public ResponseEntity<ApiResponse<SettingInfoListResponse>> findSettingList() {
        SettingInfoListResponse responseMessage = workspaceService.getSettingList();
        return ResponseEntity.ok(new ApiResponse<>(responseMessage));
    }

    @ApiOperation(value = "설정 정보 수정", tags = "only license server")
    @PutMapping("/settings")
    public ResponseEntity<ApiResponse<SettingUpdateResponse>> updateSetting(@RequestBody SettingUpdateRequest settingUpdateRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors()
                    .forEach(
                            objectError -> log.error("Parameter Error Message : [{}]", objectError));
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        SettingUpdateResponse responseMessage = workspaceService.updateSetting(settingUpdateRequest);
        return ResponseEntity.ok(new ApiResponse<>(responseMessage));
    }
}
