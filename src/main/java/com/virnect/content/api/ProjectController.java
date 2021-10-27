package com.virnect.content.api;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

import com.virnect.content.application.project.ProjectService;
import com.virnect.content.domain.EditPermission;
import com.virnect.content.domain.Mode;
import com.virnect.content.domain.SharePermission;
import com.virnect.content.domain.TargetType;
import com.virnect.content.dto.request.ProjectUpdateRequest;
import com.virnect.content.dto.request.ProjectUploadRequest;
import com.virnect.content.dto.response.ProjectDeleteResponse;
import com.virnect.content.dto.response.ProjectInfoListResponse;
import com.virnect.content.dto.response.ProjectInfoResponse;
import com.virnect.content.dto.response.ProjectUpdateResponse;
import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.common.ApiResponse;
import com.virnect.content.global.common.PageRequest;
import com.virnect.content.global.error.ErrorCode;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-08-18
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Controller
@RequestMapping("/contents/projects")
@RequiredArgsConstructor
public class ProjectController {
	private final ProjectService projectService;

	@ApiOperation(value = "프로젝트 업로드", notes = "프로젝트를 업로드합니다.")
	@PostMapping
	public ResponseEntity<ApiResponse<ProjectInfoResponse>> uploadProject(
		@RequestBody @Valid ProjectUploadRequest projectUploadRequest, BindingResult bindingResult
	) {
		log.info("[PROJECT UPLOAD] REQ : {}", projectUploadRequest.toString());
		if (bindingResult.hasErrors()) {
			log.error(
				"[FIELD ERROR] => [{}] [{}]", bindingResult.getFieldError().getField(),
				bindingResult.getFieldError().getDefaultMessage()
			);
			throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ProjectInfoResponse responseMessage = projectService.uploadProject(projectUploadRequest);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	@ApiOperation(value = "프로젝트 정보 수정", notes = "프로젝트 상세정보를 수정합니다. 공유/편집 권한이 모두 있는 사용자만 수정할 수 있습니다.")
	@PutMapping("/{projectUUID}")
	public ResponseEntity<ApiResponse<ProjectUpdateResponse>> updateProjectInfo(
		@PathVariable("projectUUID") String projectUUID,
		@RequestBody ProjectUpdateRequest projectUpdateRequest
	) {
		log.info("[PROJECT UPDATE] project uuid : {}, REQ : {}", projectUUID, projectUpdateRequest.toString());
		if (!StringUtils.hasText(projectUUID) || !StringUtils.hasText(projectUpdateRequest.getUserUUID())) {
			throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ProjectUpdateResponse responseMessage = projectService.updateProject(projectUUID, projectUpdateRequest);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	@ApiOperation(value = "프로젝트 다운로드", notes = "프로젝트를 다운로드합니다. 공유 권한이 있는 사용자만 다운로드할 수 있습니다. \n 프로젝트 식별자로 다운로드 할 수 있습니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "projectUUIDList", value = "프로젝트 식별자 목록", dataType = "string", paramType = "query", required = true, example = "", allowMultiple = true),
		@ApiImplicitParam(name = "userUUID", value = "다운로드 요청 유저 식별자", dataType = "string", paramType = "query", required = true, example = "498b1839dc29ed7bb2ee90ad6985c608"),
		@ApiImplicitParam(name = "workspaceUUID", value = "다운로드 요청 워크스페이스 식별자", dataType = "string", paramType = "query", required = true, example = "4d6eab0860969a50acbfa4599fbb5ae8"),
	})
	@GetMapping("/download")
	public ResponseEntity<byte[]> downloadProjectByUUIDList(
		@RequestParam("projectUUIDList") List<String> projectUUIDList, @RequestParam("userUUID") String userUUID,
		@RequestParam("workspaceUUID") String workspaceUUID
	) {
		log.info(
			"[PROJECT DOWNLOAD] REQ projectUUIDList : {}, userUUID : {}, workspaceUUID : {}",
			String.join(",", projectUUIDList), userUUID, workspaceUUID
		);
		if (CollectionUtils.isEmpty(projectUUIDList) || !StringUtils.hasText(userUUID) || !StringUtils.hasText(
			workspaceUUID)) {
			throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		return projectService.downloadProjectByUUIDList(projectUUIDList, userUUID, workspaceUUID);
	}

	@ApiOperation(value = "프로젝트 목록 조회", notes = "프로젝트 목록을 조회합니다. 공유 권한이 있는 프로젝트만 확인할 수 있습니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query", required = true, example = "4d6eab0860969a50acbfa4599fbb5ae8"),
		@ApiImplicitParam(name = "userUUID", value = "목록 조회 요청 유저 식별자", dataType = "string", paramType = "query", required = true, example = "498b1839dc29ed7bb2ee90ad6985c608"),
		@ApiImplicitParam(name = "share", value = "필터 - 공유 권한 정보(MEMBER, SPECIFIC_MEMBER, UPLOADER, MANAGER)", dataType = "string", paramType = "query", allowMultiple = true, allowEmptyValue = true),
		@ApiImplicitParam(name = "edit", value = "필터 - 편집 권한 정보(MEMBER, SPECIFIC_MEMBER, UPLOADER, MANAGER)", dataType = "string", paramType = "query", allowMultiple = true, allowEmptyValue = true),
		@ApiImplicitParam(name = "mode", value = "필터 - 모드 정보(TWO_DIMENSINAL, THREE_DIMENSINAL, TWO_OR_THREE_DIMENSINAL)", paramType = "query", allowMultiple = true, allowEmptyValue = true),
		@ApiImplicitParam(name = "target", value = "필터 - 타겟 타입 정보(QR, VTarget, Image, VR)", paramType = "query", allowMultiple = true, allowEmptyValue = true),
		@ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "10"),
		@ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(1부터 시작)", paramType = "query", defaultValue = "1"),
		@ApiImplicitParam(name = "sort", value = "정렬 옵션 ", paramType = "query", defaultValue = "createdDate,desc"),
		@ApiImplicitParam(name = "search", value = "검색어 옵션(프로젝트 이름)", paramType = "query", defaultValue = ""),
	})
	@GetMapping
	public ResponseEntity<ApiResponse<ProjectInfoListResponse>> getProjectList(
		@RequestParam(value = "workspaceUUID", required = true) String workspaceUUID,
		@RequestParam(value = "userUUID", required = true) String userUUID,
		@RequestParam(value = "share", required = false) List<SharePermission> sharePermissionList,
		@RequestParam(value = "edit", required = false) List<EditPermission> editPermissionList,
		@RequestParam(value = "mode", required = false) List<Mode> modeList,
		@RequestParam(value = "target", required = false) List<TargetType> targetTypeList,
		@RequestParam(value = "search", required = false) String search,
		@ApiIgnore PageRequest pageRequest
	) {
		if (!StringUtils.hasText(workspaceUUID) || !StringUtils.hasText(userUUID)) {
			throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ProjectInfoListResponse responseMessage = projectService.getProjectList(
			workspaceUUID, userUUID, sharePermissionList, editPermissionList, modeList, targetTypeList, search,
			pageRequest.of()
		);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	@ApiOperation(value = "프로젝트 상세 정보 조회", notes = "프로젝트 상세정보를 조회합니다. 공유 권한이 있는 사용자만 확인할 수 있습니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "projectUUID", value = "프로젝트 식별자", dataType = "string", paramType = "path", required = true, example = "3e0e1203-9816-46b6-8b28-608f61db6f0a"),
		@ApiImplicitParam(name = "userUUID", value = "목록 조회 요청 유저 식별자", dataType = "string", paramType = "query", required = true, example = "498b1839dc29ed7bb2ee90ad6985c608")
	})
	@GetMapping("/{projectUUID}")
	public ResponseEntity<ApiResponse<ProjectInfoResponse>> getProjectInfo(
		@PathVariable("projectUUID") String projectUUID, @RequestParam("userUUID") String userUUID
	) {
		if (!StringUtils.hasText(projectUUID) || !StringUtils.hasText(userUUID)) {
			throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ProjectInfoResponse responseMessage = projectService.getProjectInfo(projectUUID, userUUID);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	@ApiOperation(value = "프로젝트 삭제", notes = "프로젝트를 삭제합니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "projectUUID", value = "프로젝트 식별자", dataType = "string", paramType = "path", required = true, example = "7a2a56ef-e349-42ca-abb6-96a99f2ce6f9"),
		@ApiImplicitParam(name = "Authorization", value = "인증 헤더", required = true, dataType = "string", paramType = "header", defaultValue = "Bearer "),
	})
	@DeleteMapping("/{projectUUID}")
	public ResponseEntity<ApiResponse<ProjectDeleteResponse>> deleteProject(
		@PathVariable("projectUUID") String projectUUID
	) {
		String userUUID = MDC.get("userUUID");
		log.info("[PROJECT DELETE] REQ projectUUID : {}, userUUID : {}", projectUUID, userUUID);
		if (!StringUtils.hasText(projectUUID) || !StringUtils.hasText(userUUID)) {
			throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ProjectDeleteResponse responseMessage = projectService.deleteProject(projectUUID, userUUID);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

}
