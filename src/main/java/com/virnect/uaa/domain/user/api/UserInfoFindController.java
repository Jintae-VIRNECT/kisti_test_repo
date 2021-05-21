package com.virnect.uaa.domain.user.api;

import java.util.Locale;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.user.dto.request.UserEmailFindRequest;
import com.virnect.uaa.domain.user.dto.request.UserInfoAccessCheckRequest;
import com.virnect.uaa.domain.user.dto.request.UserInfoModifyRequest;
import com.virnect.uaa.domain.user.dto.request.UserPasswordChangeRequest;
import com.virnect.uaa.domain.user.dto.request.UserPasswordFindAuthCodeCheckRequest;
import com.virnect.uaa.domain.user.dto.request.UserPasswordFindAuthCodeRequest;
import com.virnect.uaa.domain.user.dto.request.UserProfileUpdateRequest;
import com.virnect.uaa.domain.user.dto.response.UserEmailFindResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoAccessCheckResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoResponse;
import com.virnect.uaa.domain.user.dto.response.UserPasswordChangeResponse;
import com.virnect.uaa.domain.user.dto.response.UserPasswordFindAuthCodeResponse;
import com.virnect.uaa.domain.user.dto.response.UserPasswordFindCodeCheckResponse;
import com.virnect.uaa.domain.user.dto.response.UserProfileUpdateResponse;
import com.virnect.uaa.domain.user.error.UserAccountErrorCode;
import com.virnect.uaa.domain.user.exception.UserServiceException;
import com.virnect.uaa.global.common.ApiResponse;

@Api(value = "사용자 정보 찾기 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserInfoFindController {

	@ApiOperation(value = "계정 정보 찾기 - 아이디", notes = "계정 정보 중 아이디를 찾고자 할때 사용하는 api 입니다.")
	@PostMapping("/find/email")
	public ResponseEntity<ApiResponse<UserEmailFindResponse>> findUserEmailRequestHandler(
		@RequestBody @Valid UserEmailFindRequest userEmailFindRequest, BindingResult result
	) {
		if (result.hasErrors() || (StringUtils.isEmpty(userEmailFindRequest.getMobile()) && StringUtils.isEmpty(
			userEmailFindRequest.getRecoveryEmail())) || userEmailFindRequest.getMobile().split("-").length >= 3) {
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new UserServiceException(UserAccountErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		UserEmailFindResponse responseMessage = userServiceImpl.userFindEmailHandler(userEmailFindRequest);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	@ApiOperation(value = "계정 정보 찾기 - 비밀번호 변경", notes = "신규 비밀번호를 설정하는 api 입니다.")
	@PutMapping(value = "/find/password")
	public ResponseEntity<ApiResponse<UserPasswordChangeResponse>> userPasswordChangeRequestHandler(
		@RequestBody @Valid UserPasswordChangeRequest passwordChangeRequest, BindingResult result
	) {
		if (result.hasErrors()) {
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new UserServiceException(UserAccountErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		UserPasswordChangeResponse responseMessage = userServiceImpl.userPasswordChangeHandler(passwordChangeRequest);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	@Profile("!onpremise")
	@ApiOperation(value = "계정 정보 찾기 - 비밀번호 재설정 코드 보내기", notes = "비밀번호를 찾기 위해 재설정 코드를 보내는 api 입니다.")
	@PostMapping("/find/password/auth")
	public ResponseEntity<ApiResponse<UserPasswordFindAuthCodeResponse>> findUserPasswordAuthCodeRequestHandler(
		@RequestBody @Valid UserPasswordFindAuthCodeRequest passwordAuthCodeRequest, Locale locale, BindingResult result
	) {
		if (result.hasErrors()) {
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new UserServiceException(UserAccountErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		UserPasswordFindAuthCodeResponse responseMessage = userServiceImpl.userPasswordFindAuthCodeSendHandler(
			passwordAuthCodeRequest, locale
		);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	@Profile("!onpremise")
	@ApiOperation(value = "계정 정보 찾기 - 비밀번호 재설정 코드 인증", notes = "이메일로 전송된 비밀번호 재설정 코드 인증을 확인하는 api 입니다.")
	@PostMapping("/find/password/check")
	public ResponseEntity<ApiResponse<UserPasswordFindCodeCheckResponse>> findUserPasswordAuthCodeCheckHandler(
		@RequestBody @Valid UserPasswordFindAuthCodeCheckRequest authCodeCheckRequest, BindingResult result
	) {
		if (result.hasErrors()) {
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new UserServiceException(UserAccountErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}

		UserPasswordFindCodeCheckResponse responseMessage = userServiceImpl.userPasswordFindAuthCodeCheckHandler(
			authCodeCheckRequest);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}
}
