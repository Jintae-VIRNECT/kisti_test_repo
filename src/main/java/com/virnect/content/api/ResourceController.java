package com.virnect.content.api;

import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.application.ResourceService;
import com.virnect.content.dto.request.FileResourceUploadRequest;
import com.virnect.content.dto.response.ContentResourceUsageInfoResponse;
import com.virnect.content.dto.response.FileResourceUploadResponse;
import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.common.ApiResponse;
import com.virnect.content.global.error.ErrorCode;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-09-02
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@RestController
@RequestMapping("/contents")
@RequiredArgsConstructor
public class ResourceController {
	private final ResourceService resourceService;

	@ApiOperation(value = "리소스 파일 업로드", notes = "컨텐츠, 프로젝트 관련 리소스 파일을 업로드합니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "file", value = "업로드 대상 파일", dataType = "__file", paramType = "form", required = true),
		@ApiImplicitParam(name = "type",value = "파일 업로드 타입", example = "CONTENT", required = true)
	})
	@PostMapping("/resources")
	public ResponseEntity<ApiResponse<FileResourceUploadResponse>> uploadFileResource(
		@ModelAttribute @Valid FileResourceUploadRequest fileResourceUploadRequest, BindingResult bindingResult
	) {
		if (bindingResult.hasErrors()) {
			log.error(
				"[FIELD ERROR] => [{}] [{}]", bindingResult.getFieldError().getField(),
				bindingResult.getFieldError().getDefaultMessage()
			);
			throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		FileResourceUploadResponse responseMessage = resourceService.uploadFileResource(
			fileResourceUploadRequest);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
}

	@ApiOperation(value = "워크스페이스 사용 용량 및 다운로드 횟수 조회")
	@ApiImplicitParams({
		@ApiImplicitParam(value = "워크스페이스 식별자", name = "workspaceId", required = true, paramType = "path", example = "4d6eab0860969a50acbfa4599fbb5ae8"),
		@ApiImplicitParam(value = "사용랑 조회 시작 일자", name = "startDate", required = true, paramType = "query", example = "2020-07-24T10:00:00"),
		@ApiImplicitParam(value = "사용랑 조회 종료 일자", name = "endDate", required = true, paramType = "query", example = "2020-08-24T23:59:59")
	})
	@GetMapping("/resources/report/{workspaceId}")
	public ResponseEntity<ApiResponse<ContentResourceUsageInfoResponse>> getContentResourceUsageInfoRequest(
		@PathVariable("workspaceId") String workspaceId,
		@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
		@RequestParam(name = "startDate") LocalDateTime startDate,
		@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
		@RequestParam(name = "endDate") LocalDateTime endDate
	) {
		if (StringUtils.isEmpty(workspaceId)) {
			throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<ContentResourceUsageInfoResponse> responseMessage = resourceService.getContentResourceUsageInfo(
			workspaceId, startDate, endDate);
		return ResponseEntity.ok(responseMessage);
	}
}
