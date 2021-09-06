package com.virnect.content.api;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.application.FileService;
import com.virnect.content.dto.request.FileResourceUploadRequest;
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
public class FileController {
	private final FileService fileService;

	@ApiOperation(value = "파일 업로드", notes = "컨텐츠, 프로젝트 관련 리소스 파일을 업로드합니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "file", value = "업로드 대상 파일", dataType = "__file", paramType = "form", required = true),
		@ApiImplicitParam(name = "type",value = "파일 업로드 타입", example = "CONTENT", required = true)
	})
	@PostMapping("/file")
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
		FileResourceUploadResponse responseMessage = fileService.uploadFileResource(
			fileResourceUploadRequest);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}
}
