package com.virnect.content.api;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import com.virnect.content.exception.ProjectServiceException;
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
	@ApiImplicitParams({
		@ApiImplicitParam(name = "project", value = "업로드 프로젝트 파일", dataType = "__file", paramType = "form", required = true)})
	@PostMapping
	public ResponseEntity<ApiResponse<ProjectInfoResponse>> uploadProject(
		@ModelAttribute @Valid ProjectUploadRequest projectUploadRequest, BindingResult bindingResult
	) {
		if (bindingResult.hasErrors()) {
			log.error(
				"[FIELD ERROR] => [{}] [{}]", bindingResult.getFieldError().getField(),
				bindingResult.getFieldError().getDefaultMessage()
			);
			throw new ProjectServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ProjectInfoResponse responseMessage = projectService.uploadProject(projectUploadRequest);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	@ApiOperation(value = "프로젝트 다운로드", notes = "프로젝트를 다운로드합니다. 공유 권한이 있는 사용자만 다운로드할 수 있습니다. \n 프로젝트 식별자로 다운로드 혹은 타겟 데이터로 다운로드할 수 있습니다.", hidden = true)
	@ApiImplicitParams({
		@ApiImplicitParam(name = "projectUUID", value = "프로젝트 식별자", dataType = "string", paramType = "query", required = false, example = ""),
		@ApiImplicitParam(name = "targetData", value = "타겟 데이터", dataType = "string", paramType = "query", required = false, example = ""),
		@ApiImplicitParam(name = "userUUID", value = "다운로드 요청 유저 식별자", dataType = "string", paramType = "query", required = true, example = ""),
	})
	@GetMapping("/download")
	public ResponseEntity<ApiResponse> downloadProjectByUUID(
		@RequestParam("projectUUID") String projectUUID, @RequestParam("targetData") String targetData,
		@RequestParam("userUUID") String userUUID
	) {
		return ResponseEntity.ok(new ApiResponse<>());
	}

	@ApiOperation(value = "프로젝트 목록 조회", notes = "프로젝트 목록을 조회합니다. 공유 권한이 있는 프로젝트만 확인할 수 있습니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query", required = true, example = "4d6eab0860969a50acbfa4599fbb5ae8"),
		@ApiImplicitParam(name = "userUUID", value = "목록 조회 요청 유저 식별자", dataType = "string", paramType = "query", required = true, example = "498b1839dc29ed7bb2ee90ad6985c608"),
		@ApiImplicitParam(name = "sharePermission", value = "공유 권한 정보", dataType = "string", paramType = "query", allowMultiple = true),
		@ApiImplicitParam(name = "editPermission", value = "편집 권한 정보", dataType = "string", paramType = "query", allowMultiple = true),
		@ApiImplicitParam(name = "mode", value = "모드 정보", dataType = "string", paramType = "query", allowMultiple = true),
		@ApiImplicitParam(name = "targetType", value = "타겟 타입 정보", dataType = "string", paramType = "query", allowMultiple = true),
		@ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "10"),
		@ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(1부터 시작)", paramType = "query", defaultValue = "1"),
		@ApiImplicitParam(name = "sort", value = "정렬 옵션 ", paramType = "query", defaultValue = "createdDate,desc"),
	})
	@GetMapping
	public ResponseEntity<ApiResponse<ProjectInfoListResponse>> getProjectList(
		@RequestParam(value = "workspaceUUID", required = true) String workspaceUUID,
		@RequestParam(value = "userUUID", required = true) String userUUID,
		@RequestParam(value = "sharePermission", required = false) List<SharePermission> sharePermissionList,
		@RequestParam(value = "editPermission", required = false) List<EditPermission> editPermissionList,
		@RequestParam(value = "mode", required = false) List<Mode> modeList,
		@RequestParam(value = "targetType", required = false) List<TargetType> targetTypeList,
		@ApiIgnore PageRequest pageRequest
	) {
		if (!StringUtils.hasText(workspaceUUID) || !StringUtils.hasText(userUUID)) {
			throw new ProjectServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ProjectInfoListResponse responseMessage = projectService.getProjectList(
			workspaceUUID, userUUID, sharePermissionList, editPermissionList, modeList, targetTypeList,
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
			throw new ProjectServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ProjectInfoResponse responseMessage = projectService.getProjectInfo(projectUUID, userUUID);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	@ApiOperation(value = "프로젝트 정보 수정 - 업데이트 또는 덮어쓰기", notes = "프로젝트 상세정보를 수정합니다. 편집 권한이 있는 사용자만 수정할 수 있습니다.", hidden = true)
	@ApiImplicitParams({
		@ApiImplicitParam(name = "projectUUID", value = "프로젝트 식별자", dataType = "string", paramType = "path", required = true),
		@ApiImplicitParam(name = "userUUID", value = "목록 조회 요청 유저 식별자", dataType = "string", paramType = "query", required = true),
	})
	@PutMapping("/{projectUUID}")
	public ResponseEntity<ApiResponse> updateProjectInfo(
		@PathVariable("projectUUID") String proejctUUID, @RequestParam("userUUID") String userUUID,
		@ModelAttribute ProjectUpdateRequest projectUpdateRequest
	) {
		return ResponseEntity.ok(new ApiResponse<>());
	}

	@ApiOperation(value = "프로젝트 삭제", notes = "프로젝트를 삭제합니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "projectUUID", value = "프로젝트 식별자", dataType = "string", paramType = "path")
	})
	@DeleteMapping("/{projectUUID}")
	public ResponseEntity<ApiResponse<ProjectDeleteResponse>> deleteProject(
		@PathVariable("projectUUID") String projectUUID
	) {
		ProjectDeleteResponse responseMessage = projectService.deleteProject(projectUUID);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

}
