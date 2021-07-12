package com.virnect.uaa.domain.user.api;

import static com.virnect.uaa.global.common.LogMessagePrefix.*;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.user.application.OffUserInformationService;
import com.virnect.uaa.domain.user.dto.request.MemberPasswordUpdateRequest;
import com.virnect.uaa.domain.user.dto.request.RegisterMemberRequest;
import com.virnect.uaa.domain.user.dto.request.UserIdentityCheckRequest;
import com.virnect.uaa.domain.user.dto.response.MemberPasswordUpdateResponse;
import com.virnect.uaa.domain.user.dto.response.UserDeleteResponse;
import com.virnect.uaa.domain.user.dto.response.UserEmailExistCheckResponse;
import com.virnect.uaa.domain.user.dto.response.UserIdentityCheckResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoResponse;
import com.virnect.uaa.domain.user.error.UserAccountErrorCode;
import com.virnect.uaa.domain.user.exception.UserServiceException;
import com.virnect.uaa.global.common.ApiResponse;

@Api(value = "OnPremise 환경 전용 API")
@Slf4j
@Profile("onpremise")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class OffUserInfoController {
	private final OffUserInformationService offUserInformationService;

	@ApiImplicitParams(
		@ApiImplicitParam(name = "serviceID", value = "요청 서버 명", paramType = "header", example = "workspace-server")
	)
	@ApiOperation(value = "멤버 등록 API")
	@PostMapping(value = "/register/member")
	public ResponseEntity<ApiResponse<UserInfoResponse>> registerMemberRequestHandler(
		@RequestBody @Valid RegisterMemberRequest registerMemberRequest,
		@RequestHeader("serviceID") String serviceID, BindingResult result
	) {
		if (result.hasErrors() || StringUtils.isEmpty(serviceID) || !serviceID.equals("workspace-server")) {
			log.error("SERVICE_ID:[{}]", serviceID);
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new UserServiceException(UserAccountErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		UserInfoResponse userInfoResponse = offUserInformationService.registerNewMember(registerMemberRequest);
		return ResponseEntity.ok(new ApiResponse<>(userInfoResponse));
	}

	@ApiOperation(value = "멤버 삭제 API")
	@ApiImplicitParams(
		@ApiImplicitParam(name = "serviceID", value = "요청 서버 명", paramType = "header", example = "workspace-server")
	)
	@DeleteMapping("/{userUUID}")
	public ResponseEntity<ApiResponse<UserDeleteResponse>> userDeleteRequestHandler(
		@PathVariable("userUUID") String userUUID, @RequestHeader("serviceID") String serviceID
	) {
		if (StringUtils.isEmpty(userUUID) || StringUtils.isEmpty(serviceID) || !serviceID.equals("workspace-server")) {
			log.error("SERVICE_ID:[{}]", serviceID);
			throw new UserServiceException(UserAccountErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		UserDeleteResponse userDeleteResponse = offUserInformationService.deleteMemberUser(userUUID);
		return ResponseEntity.ok(new ApiResponse<>(userDeleteResponse));
	}

	@ApiOperation(value = "계정 아이디 확인 API")
	@GetMapping("/exist")
	public ResponseEntity<ApiResponse<UserEmailExistCheckResponse>> userEmailExistCheckRequestHandler(
		@RequestParam("email") String email
	) {
		if (StringUtils.isEmpty(email)) {
			throw new UserServiceException(UserAccountErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		UserEmailExistCheckResponse userEmailExistCheckResponse = offUserInformationService.userEmailDuplicateCheck(email);
		return ResponseEntity.ok(new ApiResponse<>(userEmailExistCheckResponse));
	}

	@ApiOperation(value = "비밀번호 변경 질의 응답 확인 API")
	@PostMapping("/password/identity/check")
	public ResponseEntity<ApiResponse<UserIdentityCheckResponse>> userIdentityCheckRequestHandler(
		@RequestBody UserIdentityCheckRequest userIdentityCheckRequest, BindingResult result
	) {
		if (result.hasErrors()) {
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new UserServiceException(UserAccountErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		UserIdentityCheckResponse userIdentityCheckResponse = offUserInformationService.verifyPasswordResetQuestion(
			userIdentityCheckRequest);
		return ResponseEntity.ok(new ApiResponse<>(userIdentityCheckResponse));
	}

	@ApiOperation(value = "멤버 비밀번호 변경 API")
	@ApiImplicitParams(
		@ApiImplicitParam(name = "serviceID", value = "요청 서버 명", paramType = "header", example = "workspace-server")
	)
	@PostMapping(value = "/member/password")
	public ResponseEntity<ApiResponse<MemberPasswordUpdateResponse>> memberUserPasswordChangeRequest(
		@RequestBody @Valid MemberPasswordUpdateRequest memberPasswordUpdateRequest,
		@RequestHeader("serviceID") String serviceID, BindingResult result
	) {
		if (result.hasErrors() || StringUtils.isEmpty(serviceID) || !serviceID.equals("workspace-server")) {
			log.error("SERVICE_ID:[{}]", serviceID);
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new UserServiceException(UserAccountErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		MemberPasswordUpdateResponse responseMessage = offUserInformationService.updateMemberPassword(memberPasswordUpdateRequest);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}
}
