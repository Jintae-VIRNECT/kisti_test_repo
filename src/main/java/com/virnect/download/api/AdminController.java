package com.virnect.download.api;

import javax.validation.Valid;

import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.download.application.download.AdminService;
import com.virnect.download.dto.request.AdminAppUploadRequest;
import com.virnect.download.dto.response.AdminAppDeleteResponse;
import com.virnect.download.dto.response.AdminAppUploadResponse;
import com.virnect.download.exception.AppServiceException;
import com.virnect.download.global.common.ApiResponse;
import com.virnect.download.global.error.ErrorCode;

@Profile({"onpremise", "local", "test"})
@Slf4j
@RestController
@RequestMapping("/download/app")
@RequiredArgsConstructor
public class AdminController {
	private final AdminService adminService;

	@ApiOperation(value = "관리자 앱 등록 API", notes = "설치파일을 업로드 합니다."
		+ "\n 파일 이름 명명 규칙 : {product_name}_{device_type}_{version_code}.{extension}", tags = "onpremise-controller")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "uploadAppFile", value = "업로드 앱 파일", paramType = "form", dataType = "__file", required = true),
		@ApiImplicitParam(name = "Authorization", value = "Authorization Header", required = true, dataType = "string", paramType = "header", defaultValue = "Bearer "),})
	@PostMapping("/register/admin")
	public ResponseEntity<ApiResponse<AdminAppUploadResponse>> adminApplicationRegister(
		@ModelAttribute @Valid AdminAppUploadRequest adminAppUploadRequest, BindingResult result
	) {
		String userUUID = MDC.get("userUUID");
		log.info("[ADMIN_APP_UPLOAD] REQ : {}, USER UUID : {},  FILE_NAME : {}", adminAppUploadRequest.toString(),
			userUUID, adminAppUploadRequest.getUploadAppFile().getOriginalFilename()
		);
		if (result.hasErrors() || StringUtils.isEmpty(userUUID)) {
			result.getAllErrors().forEach(message -> log.error("[PARAMETER ERROR]:: {}", message));
			throw new AppServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		AdminAppUploadResponse responseMessage = adminService.uploadApplication(adminAppUploadRequest, userUUID);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	@ApiOperation(value = "관리자 앱 삭제 API", notes = "업로드 된 파일을 삭제 합니다.", tags = "onpremise-controller")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "Authorization", value = "Authorization Header", required = true, dataType = "string", paramType = "header", defaultValue = "Bearer "),})
	@DeleteMapping("/{appUUID}/admin")
	public ResponseEntity<ApiResponse<AdminAppDeleteResponse>> adminApplicationDelete(
		@PathVariable("appUUID") String appUUID
	) {
		String userUUID = MDC.get("userUUID");
		log.info("[ADMIN_APP_UPLOAD] APP : {}, USER : {},", appUUID, userUUID);
		if (StringUtils.isEmpty(userUUID) || StringUtils.isEmpty(appUUID)) {
			throw new AppServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		AdminAppDeleteResponse responseMessage = adminService.deleteApplication(appUUID, userUUID);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}
}