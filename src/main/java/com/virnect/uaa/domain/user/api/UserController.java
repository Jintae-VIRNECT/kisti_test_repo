package com.virnect.uaa.domain.user.api;

import java.beans.PropertyEditorSupport;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

import com.virnect.uaa.domain.user.application.UserServiceImpl;
import com.virnect.uaa.domain.user.dto.request.MemberUserPasswordChangeRequest;
import com.virnect.uaa.domain.user.dto.request.RegisterDetailsRequest;
import com.virnect.uaa.domain.user.dto.request.RegisterMemberRequest;
import com.virnect.uaa.domain.user.dto.request.RegisterRequest;
import com.virnect.uaa.domain.user.dto.request.UserEmailFindRequest;
import com.virnect.uaa.domain.user.dto.request.UserIdentityCheckRequest;
import com.virnect.uaa.domain.user.dto.request.UserInfoAccessCheckRequest;
import com.virnect.uaa.domain.user.dto.request.UserInfoModifyRequest;
import com.virnect.uaa.domain.user.dto.request.UserPasswordChangeRequest;
import com.virnect.uaa.domain.user.dto.request.UserPasswordFindAuthCodeCheckRequest;
import com.virnect.uaa.domain.user.dto.request.UserPasswordFindAuthCodeRequest;
import com.virnect.uaa.domain.user.dto.request.UserProfileUpdateRequest;
import com.virnect.uaa.domain.user.dto.request.UserSecessionRequest;
import com.virnect.uaa.domain.user.dto.response.InviteUserInfoResponse;
import com.virnect.uaa.domain.user.dto.response.MemberUserPasswordChangeResponse;
import com.virnect.uaa.domain.user.dto.response.UserAccessHistoryResponse;
import com.virnect.uaa.domain.user.dto.response.UserDeleteResponse;
import com.virnect.uaa.domain.user.dto.response.UserDetailsInfoResponse;
import com.virnect.uaa.domain.user.dto.response.UserEmailExistCheckResponse;
import com.virnect.uaa.domain.user.dto.response.UserEmailFindResponse;
import com.virnect.uaa.domain.user.dto.response.UserIdentityCheckResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoAccessCheckResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoListOnlyResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoListResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoResponse;
import com.virnect.uaa.domain.user.dto.response.UserPasswordChangeResponse;
import com.virnect.uaa.domain.user.dto.response.UserPasswordFindAuthCodeResponse;
import com.virnect.uaa.domain.user.dto.response.UserPasswordFindCodeCheckResponse;
import com.virnect.uaa.domain.user.dto.response.UserProfileUpdateResponse;
import com.virnect.uaa.domain.user.dto.response.UserSecessionResponse;
import com.virnect.uaa.domain.user.error.UserAccountErrorCode;
import com.virnect.uaa.domain.user.exception.UserServiceException;
import com.virnect.uaa.global.common.ApiResponse;
import com.virnect.uaa.global.common.PageRequest;

@Api(value = "사용자 정보 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
	private static final String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
	private final UserServiceImpl userServiceImpl;

	@InitBinder(value = {"registerRequest", "userProfileUpdateRequest"})
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(MultipartFile.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				setValue(null);
			}
		});
	}

	/**
	 * 회원가입 처리
	 *
	 * @param registerRequest - 회원가입 요청
	 * @param result          - 회원가입 결과
	 */
	@ApiOperation(value = "회원가입", notes = "회원가입 api 입니다.", hidden = true)
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<UserInfoResponse>> registerRequestHandler(
		@ModelAttribute @Valid RegisterRequest registerRequest, BindingResult result
	) {
		if (result.hasErrors()) {
			log.info("Request: {}", registerRequest.toString());
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new UserServiceException(UserAccountErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		UserInfoResponse responseMessage = userServiceImpl.register(registerRequest);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	@ApiOperation(value = "회원가입 추가정보 입력", notes = "회원가입 이후 추가 정보 입력 시 호출되는 api 입니다.", hidden = true)
	@PostMapping("/register/detail")
	public ResponseEntity<ApiResponse<UserInfoResponse>> registerWithDetailsRequestHandler(
		@ModelAttribute @Valid RegisterDetailsRequest registerDetailsRequest, BindingResult result
	) {
		if (result.hasErrors()) {
			log.error("REQUEST: {}", registerDetailsRequest.toString());
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new UserServiceException(UserAccountErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		UserInfoResponse responseMessage = userServiceImpl.registerWithDetails(registerDetailsRequest);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	/**
	 * 사용자 정보 리스트 조회 처리
	 *
	 * @param search - 조회 조건
	 * @return - 사용자 데이터 목록
	 */
	@ApiOperation(value = "사용자 정보 리스트 조회", notes = "사용자 정보 목록을 조회하는 api 입니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "search", value = "검색어(이메일/사용자명)", dataType = "string", allowEmptyValue = true, defaultValue = "smic"),
		@ApiImplicitParam(name = "paging", value = "검색 결과 페이지네이션 여부", dataType = "boolean", allowEmptyValue = true, defaultValue = "false"),
		@ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "2"),
		@ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(0부터 시작)", paramType = "query", defaultValue = "0"),
		@ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "createdDate,desc"),
	})
	@GetMapping
	public ResponseEntity<ApiResponse<UserInfoListResponse>> getUserInfoList(
		@RequestParam(name = "search", required = false) String search, @RequestParam("paging") boolean paging,
		@ApiIgnore PageRequest pageable
	) {
		UserInfoListResponse responseMessage = userServiceImpl.getUserInfoList(search, paging, pageable.of());
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	/**
	 * 사용자 정보 조회
	 *
	 * @param userId - 조회 대상 사용자 식별 번호
	 * @return - 사용자 정보
	 */
	@ApiOperation(value = "사용자 정보 조회", notes = "특정 사용자의 정보를 조회하는 api 입니다.")
	@ApiImplicitParam(name = "userId", value = "사용자 고유 번호(UUID)", dataType = "string", paramType = "path", required = true, defaultValue = "498b1839dc29ed7bb2ee90ad6985c608")
	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfo(@PathVariable("userId") String userId) {
		if (userId.isEmpty()) {
			throw new UserServiceException(UserAccountErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		UserInfoResponse responseMessage = userServiceImpl.getUserInfoByUserId(userId);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	@ApiOperation(value = "이메일로 사용자 정보 조회", notes = "워크스페이스 초대를 위해 이메일을 통해 사용자 정보를 조호할 수 있는 api 입니다.")
	@GetMapping("/invite")
	public ResponseEntity<ApiResponse<InviteUserInfoResponse>> getInviteUserInfo(
		@RequestParam(value = "email", required = false) String email
	) {
		if (StringUtils.isEmpty(email)) {
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_INFO);
		}
		InviteUserInfoResponse responseMessage = userServiceImpl.getInviteUserInfoList(email);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	@ApiOperation(value = "사용자 식별자 배열로 사용자 정보 조회", notes = "사용자 식별자 배열을 통해 사용자 정보를 조회할 수 있는 api 입니다.")
	@GetMapping("/infoList")
	public ResponseEntity<ApiResponse<UserInfoListOnlyResponse>> getUserInfoByUUIDListRequest(
		@RequestParam(value = "uuid", required = false) String[] uuid
	) {
		if (uuid == null || uuid.length <= 0) {
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_INFO);
		}
		UserInfoListOnlyResponse userInfoListOnlyResponse = userServiceImpl.getUserInfoListByUUIDList(uuid);
		return ResponseEntity.ok(new ApiResponse<>(userInfoListOnlyResponse));
	}

	/**
	 * 사용자 정보 조회
	 *
	 * @param request - 정보 조회 요청
	 * @return
	 */
	@ApiOperation(value = "내 정보 조회(authorization 토큰을 전달할 시 사용)")
	@GetMapping("/info")
	public ResponseEntity<ApiResponse<UserDetailsInfoResponse>> getUserDetailsInfoRequestHandler(
		HttpServletRequest request
	) {
		UserDetailsInfoResponse responseMessage = userServiceImpl.getUserDetailsInfo(request);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
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
	@ApiOperation(value = "전체 사용자 현황 조회", notes = "어드민 서비스에서 관리자가 전체 사용자 현황을 조회하는 데에 사용하는 api 입니다.", tags = "admin server only")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "2"),
		@ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(0부터 시작)", paramType = "query", defaultValue = "0"),
		@ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "createdDate,desc"),
	})
	@GetMapping("/all")
	public ResponseEntity<ApiResponse<UserInfoListResponse>> findAllUserInfoRequestHandler(
		@ApiIgnore Pageable pageable
	) {
		UserInfoListResponse responseMessage = userServiceImpl.findAllUserInfo(pageable);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	@ApiOperation(value = "사용자 정보 목록 조회(Workspace 서버에서 사용)", notes = "타 서버에서 사용하는 api 입니다.", tags = "workspace server only")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "search", value = "닉네임,이메일", dataType = "string", paramType = "query", defaultValue = ""),
		@ApiImplicitParam(name = "workspaceUserIdList", value = "사용자 식별번호", dataType = "array", paramType = "body", required = true, defaultValue = "[\"498b1839dc29ed7bb2ee90ad6985c608\"]"),
	})
	@PostMapping("/list")
	public ResponseEntity<ApiResponse<UserInfoListResponse>> getUsersInfoList(
		@RequestParam(name = "search", required = false) String search, @RequestBody String[] workspaceUserIdList
	) {
		if (workspaceUserIdList == null || workspaceUserIdList.length <= 0) {
			throw new UserServiceException(UserAccountErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		UserInfoListResponse responseMessage = userServiceImpl.getUsersInfoList(search, workspaceUserIdList);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	/**
	 * 사용자 정보 조회
	 *
	 * @param userId - 조회 대상 사용자 식별 번호
	 * @return - 사용자 정보
	 */
	@Profile("!onpremise")
	@ApiOperation(value = "사용자 정보 조회", notes = "특정 사용자의 정보를 조회하는 api 입니다.", tags = "billing server only")
	@ApiImplicitParam(name = "userId", value = "사용자 대표 식별자", dataType = "string", type = "path", required = true, defaultValue = "1")
	@GetMapping("/billing/{userId}")
	public ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfoByUserIdFromBillingSystem(
		@PathVariable("userId") long userId
	) {
		if (userId <= 0) {
			throw new UserServiceException(UserAccountErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		UserInfoResponse responseMessage = userServiceImpl.getUserInfoByUserId(userId);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	@ApiOperation(value = "계정 접속 이력 정보 조회", notes = "내 계정으로 로그인했던 이력정보를 조회하는 api 입니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "userId", value = "사용자 대표 식별자", dataType = "string", paramType = "path", required = true, defaultValue = "498b1839dc29ed7bb2ee90ad6985c608"),
		@ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "20"),
		@ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(1부터 시작)", paramType = "query", defaultValue = "1"),
		@ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "createdDate,desc"),
	})
	@GetMapping("/access/{userId}")
	public ResponseEntity<ApiResponse<UserAccessHistoryResponse>> getUserAccessDeviceInfoList(
		@PathVariable("userId") String userId, @ApiIgnore PageRequest pageable
	) {
		if (StringUtils.isEmpty(userId)) {
			throw new UserServiceException(UserAccountErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		UserAccessHistoryResponse responseMessage = userServiceImpl.getUserAccessDeviceHistory(userId, pageable.of());
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	@Profile("!onpremise")
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

	@Profile("onpremise")
	@ApiImplicitParams(
		@ApiImplicitParam(name = "serviceID", value = "요청 서버 명", paramType = "header", example = "workspace-server")
	)
	@ApiOperation(value = "멤버 등록 API", tags = "onpremise 추가 API")
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
		UserInfoResponse userInfoResponse = userServiceImpl.registerNewMember(registerMemberRequest);
		return ResponseEntity.ok(new ApiResponse<>(userInfoResponse));
	}

	@Profile("onpremise")
	@ApiOperation(value = "멤버 삭제 API", tags = "onpremise 추가 API")
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
		UserDeleteResponse userDeleteResponse = userServiceImpl.deleteMemberUser(userUUID);
		return ResponseEntity.ok(new ApiResponse<>(userDeleteResponse));
	}

	@Profile("onpremise")
	@ApiOperation(value = "계정 아이디 확인 API", tags = "onpremise 추가 API")
	@GetMapping("/exist")
	public ResponseEntity<ApiResponse<UserEmailExistCheckResponse>> userEmailExistCheckRequestHandler(
		@RequestParam("email") String email
	) {
		if (StringUtils.isEmpty(email)) {
			throw new UserServiceException(UserAccountErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		UserEmailExistCheckResponse userEmailExistCheckResponse = userServiceImpl.userEmailExistCheck(email);
		return ResponseEntity.ok(new ApiResponse<>(userEmailExistCheckResponse));
	}

	@Profile("onpremise")
	@ApiOperation(value = "비밀번호 변경 질의 응답 확인 API", tags = "onpremise 추가 API")
	@PostMapping("/password/identity/check")
	public ResponseEntity<ApiResponse<UserIdentityCheckResponse>> userIdentityCheckRequestHandler(
		@RequestBody UserIdentityCheckRequest userIdentityCheckRequest, BindingResult result
	) {
		if (result.hasErrors()) {
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new UserServiceException(UserAccountErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		UserIdentityCheckResponse userIdentityCheckResponse = userServiceImpl.userIdentityCheck(
			userIdentityCheckRequest);
		return ResponseEntity.ok(new ApiResponse<>(userIdentityCheckResponse));
	}

	@Profile("onpremise")
	@ApiOperation(value = "멤버 비밀번호 변경 API", tags = "onpremise 추가 API")
	@ApiImplicitParams(
		@ApiImplicitParam(name = "serviceID", value = "요청 서버 명", paramType = "header", example = "workspace-server")
	)
	@PostMapping(value = "/member/password")
	public ResponseEntity<ApiResponse<MemberUserPasswordChangeResponse>> memberUserPasswordChangeRequest(
		@RequestBody @Valid MemberUserPasswordChangeRequest memberUserPasswordChangeRequest,
		@RequestHeader("serviceID") String serviceID, BindingResult result
	) {
		if (result.hasErrors() || StringUtils.isEmpty(serviceID) || !serviceID.equals("workspace-server")) {
			log.error("SERVICE_ID:[{}]", serviceID);
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new UserServiceException(UserAccountErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		MemberUserPasswordChangeResponse responseMessage = userServiceImpl.memberUserPasswordChangeHandler(
			memberUserPasswordChangeRequest);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}
}
