package com.virnect.uaa.domain.user.api;

import java.beans.PropertyEditorSupport;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.user.dto.request.UserInfoAccessCheckRequest;
import com.virnect.uaa.domain.user.dto.request.UserInfoModifyRequest;
import com.virnect.uaa.domain.user.dto.request.UserProfileUpdateRequest;
import com.virnect.uaa.domain.user.dto.request.UserSecessionRequest;
import com.virnect.uaa.domain.user.dto.response.UserInfoAccessCheckResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoResponse;
import com.virnect.uaa.domain.user.dto.response.UserProfileUpdateResponse;
import com.virnect.uaa.domain.user.dto.response.UserSecessionResponse;
import com.virnect.uaa.domain.user.error.UserAccountErrorCode;
import com.virnect.uaa.domain.user.exception.UserServiceException;
import com.virnect.uaa.global.common.ApiResponse;


@Api(value = "사용자 정보 수정 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserInfoUpdateController {


	@InitBinder(value = {"userProfileUpdateRequest"})
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(MultipartFile.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				setValue(null);
			}
		});
	}


	@ApiOperation(value = "개인 정보 접근 인증", notes = "사용자 개인 정보를 확인하기 위해 비밀번호 인증을 받아 처리하는 api 입니다.")
	@PostMapping("/{userId}/access")
	@ApiImplicitParam(name = "userId", value = "사용자 식별 번호", required = true, example = "498b1839dc29ed7bb2ee90ad6985c608")
	public ResponseEntity<ApiResponse<UserInfoAccessCheckResponse>> userInfoAccessCheckRequestHandler(
		@PathVariable("userId") String userId,
		@RequestBody @Valid UserInfoAccessCheckRequest userInfoAccessCheckRequest, BindingResult result
	) {
		if (result.hasErrors()) {
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new UserServiceException(UserAccountErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		UserInfoAccessCheckResponse userInfoAccessCheckResponse = userServiceImpl.userInfoAccessCheck(
			userId,
			userInfoAccessCheckRequest
		);
		return ResponseEntity.ok(new ApiResponse<>(userInfoAccessCheckResponse));
	}

	/**
	 * 사용자 프로필 수정 요청 처리
	 * @param userProfileUpdateRequest - 프로필 수정 요청 정보
	 * @param userId - 사용자 식별자
	 * @param result - 프로필 수정 요청 정보 검증 결과
	 * @return - 프로필 이미지 수정 결과
	 */
	@ApiOperation(value = "사용자 프로필 업데이트", notes = "사용자 개인 정보를 수정하는 페이지에서 프로필 이미지를 변경하는 api 입니다.")
	@PostMapping("/{userId}/profile")
	public ResponseEntity<ApiResponse<UserProfileUpdateResponse>> userProfileUpdateRequestHandler(
		@ModelAttribute UserProfileUpdateRequest userProfileUpdateRequest, @PathVariable("userId") String userId,
		BindingResult result
	) {
		if (result.hasErrors()) {
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new UserServiceException(UserAccountErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		UserProfileUpdateResponse profileUpdateResponse = userServiceImpl.profileImageUpdate(
			userId,
			userProfileUpdateRequest
		);
		return ResponseEntity.ok(new ApiResponse<>(profileUpdateResponse));
	}

	@ApiOperation(value = "개인 정보 수정", notes = "사용자 개인 정보를 수정하는 api 입니다.")
	@PostMapping("/{userId}")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "userId", value = "사용자 식별 번호"),
	})
	public ResponseEntity<ApiResponse<UserInfoResponse>> modifyUserInfoRequestHandler(
		@PathVariable("userId") String userUUID, @RequestBody @Valid UserInfoModifyRequest userInfoModifyRequest,
		BindingResult result
	) {
		if (result.hasErrors()) {
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new UserServiceException(UserAccountErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		UserInfoResponse modifiedUserInfoResponse = userServiceImpl.modifyUserInfo(userUUID, userInfoModifyRequest);
		return ResponseEntity.ok(new ApiResponse<>(modifiedUserInfoResponse));
	}

	@ApiOperation(value = "회원 탈퇴(회원 탈퇴 API 입니다. 해당 회원의 마스터 워크스페이스 관련 정보는 모두 삭제되며, 정기 결제도 취소됩니다.)")
	@PostMapping("/secession")
	public ResponseEntity<ApiResponse<UserSecessionResponse>> userSecessionRequest(
		@RequestBody UserSecessionRequest userSecessionRequest, BindingResult result
	) {
		if (result.hasErrors()) {
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new UserServiceException(UserAccountErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		UserSecessionResponse responseMessage = userServiceImpl.userSecessionRequestHandler(userSecessionRequest);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

}
