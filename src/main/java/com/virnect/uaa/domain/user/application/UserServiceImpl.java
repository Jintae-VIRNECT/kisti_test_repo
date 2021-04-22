package com.virnect.uaa.domain.user.application;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.security.UserDetailsImpl;
import com.virnect.uaa.domain.auth.account.dao.UserOTPRepository;
import com.virnect.uaa.domain.user.dao.EmailAuthRepository;
import com.virnect.uaa.domain.user.dao.SecessionUserRepository;
import com.virnect.uaa.domain.user.dao.UserPasswordAuthCodeRepository;
import com.virnect.uaa.domain.user.dao.user.UserRepository;
import com.virnect.uaa.domain.user.dao.useraccesslog.UserAccessLogRepository;
import com.virnect.uaa.domain.user.dao.userpermission.UserPermissionRepository;
import com.virnect.uaa.domain.user.domain.EmailAuth;
import com.virnect.uaa.domain.user.domain.Language;
import com.virnect.uaa.domain.user.domain.PasswordInitAuthCode;
import com.virnect.uaa.domain.user.domain.SecessionUser;
import com.virnect.uaa.domain.user.domain.Status;
import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.domain.user.domain.UserAccessLog;
import com.virnect.uaa.domain.user.domain.UserType;
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
import com.virnect.uaa.domain.user.dto.response.InviteUserDetailInfoResponse;
import com.virnect.uaa.domain.user.dto.response.InviteUserInfoResponse;
import com.virnect.uaa.domain.user.dto.response.LoginResponse;
import com.virnect.uaa.domain.user.dto.response.MemberUserPasswordChangeResponse;
import com.virnect.uaa.domain.user.dto.response.PageMetadataResponse;
import com.virnect.uaa.domain.user.dto.response.UserAccessDeviceInfoResponse;
import com.virnect.uaa.domain.user.dto.response.UserAccessHistoryResponse;
import com.virnect.uaa.domain.user.dto.response.UserDeleteResponse;
import com.virnect.uaa.domain.user.dto.response.UserDetailsInfoResponse;
import com.virnect.uaa.domain.user.dto.response.UserEmailExistCheckResponse;
import com.virnect.uaa.domain.user.dto.response.UserEmailFindInfoResponse;
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
import com.virnect.uaa.global.security.token.JwtPayload;
import com.virnect.uaa.global.security.token.JwtTokenProvider;
import com.virnect.uaa.infra.email.EmailMessage;
import com.virnect.uaa.infra.email.EmailService;
import com.virnect.uaa.infra.file.Default;
import com.virnect.uaa.infra.file.FileService;
import com.virnect.uaa.infra.rest.billing.PayApiService;
import com.virnect.uaa.infra.rest.content.ContentRestService;
import com.virnect.uaa.infra.rest.content.dto.ContentSecessionResponse;
import com.virnect.uaa.infra.rest.license.LicenseRestService;
import com.virnect.uaa.infra.rest.license.dto.LicenseSecessionResponse;
import com.virnect.uaa.infra.rest.process.ProcessRestService;
import com.virnect.uaa.infra.rest.process.dto.TaskSecessionResponse;
import com.virnect.uaa.infra.rest.remote.RemoteRestService;
import com.virnect.uaa.infra.rest.remote.dto.RemoteSecessionResponse;
import com.virnect.uaa.infra.rest.workspace.WorkspaceRestService;
import com.virnect.uaa.infra.rest.workspace.dto.WorkspaceInfoListResponse;
import com.virnect.uaa.infra.rest.workspace.dto.WorkspaceInfoResponse;
import com.virnect.uaa.infra.rest.workspace.dto.WorkspaceSecessionResponse;

/**
 * Project: user
 * DATE: 2020-01-08
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
	private final ModelMapper modelMapper;
	private final PasswordEncoder passwordEncoder;
	private final WorkspaceRestService workspaceRestService;
	private final JwtTokenProvider jwtTokenProvider;
	private final FileService fileService;
	private final UserRepository userRepository;
	private final EmailAuthRepository emailAuthRepository;
	private final UserPasswordAuthCodeRepository userPasswordAuthCodeRepository;
	private final SecessionUserRepository secessionUserRepository;
	private final UserAccessLogRepository userAccessLogRepository;
	private final UserPermissionRepository userPermissionRepository;
	private final UserOTPRepository userOTPRepository;
	private final EmailService emailService;
	private final SpringTemplateEngine templateEngine;
	private final ContentRestService contentRestService;
	private final ProcessRestService processRestService;
	private final LicenseRestService licenseRestService;
	private final PayApiService payApiService;
	private final RemoteRestService remoteRestService;
	private final MessageSource messageSource;

	private final ApplicationContext applicationContext;

	/**
	 * 사용자 로그인 요청 처리
	 *
	 * @param uuid - 사용자 고유 번호
	 * @return - 로그인 처리 완료 정보 ( 사용자 정보 + 사용자 소속 워크스페이스 정보 )
	 */
	@Override
	public LoginResponse login(String uuid) {
		UserInfoResponse userInfo = getUserInfoByUUID(uuid);
		ApiResponse<WorkspaceInfoListResponse> responseMessage = workspaceRestService.getMyWorkspaceInfoList(
			uuid, 50);
		return new LoginResponse(userInfo, responseMessage.getData().getWorkspaceList());
	}

	/**
	 * 사용자 UUID로 사용자 정보 조회
	 *
	 * @param userUUID - 사용자 고유 식별번호
	 * @return - 사용자 정보
	 */
	@Override
	public UserInfoResponse getUserInfoByUUID(String userUUID) {
		log.info("USER INFO RETRIEVE BY UUID => [{}]", userUUID);
		Optional<SecessionUser> secessionUser = secessionUserRepository.findByUserUUID(userUUID);
		if (secessionUser.isPresent()) {
			UserInfoResponse secessionUserInfoResponse = new UserInfoResponse();
			secessionUserInfoResponse.setSecessionUserInfo(secessionUser.get());
			return secessionUserInfoResponse;
		}
		User user = userRepository.findByUuid(userUUID)
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND));
		return convertUserEntityToUserInfoDto(user);
	}

	/**
	 * 사용자 ID로 사용자 정보 조회
	 *
	 * @param id - 사용자 고유 식별번호
	 * @return - 사용자 정보
	 */
	private UserInfoResponse getUserInfoByID(long id) {
		log.info("USER INFO RETRIEVE BY ID => [{}]", id);
		User user = userRepository.findById(id)
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND));
		return convertUserEntityToUserInfoDto(user);
	}

	/**
	 * 사용자 정보 조회 요청 처리
	 *
	 * @param uuid - 사용자 고유 식별번호
	 * @return - 사용자 정보
	 */
	@Override
	public UserInfoResponse getUserInfoByUserId(String uuid) {
		return getUserInfoByUUID(uuid);
	}

	/**
	 * 사용자 정보 조회 요청 처리
	 *
	 * @param id - 사용자 대표 식별자
	 * @return - 사용자 정보
	 */
	@Override
	public UserInfoResponse getUserInfoByUserId(long id) {
		return getUserInfoByID(id);
	}

	/**
	 * 회원가입 요청 처리
	 *
	 * @param registerRequest - 회원가입 요청 데이터
	 * @return - 등록된 신규 사용자 정보
	 */
	@Override
	@Transactional
	public UserInfoResponse register(RegisterRequest registerRequest) {
		// 1. 회원가입 세션 확인
		EmailAuth emailAuth = emailAuthRepository.findById(registerRequest.getEmail())
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_REGISTER_SESSION_EXPIRE));

		if (!emailAuth.getSessionCode().equals(registerRequest.getSessionCode())) {
			throw new UserServiceException(UserAccountErrorCode.ERR_REGISTER_SESSION_EXPIRE);
		}

		// 2. 이메일 중복 확인
		boolean hasDuplicateEmail = userRepository.existsByEmail(registerRequest.getEmail());

		if (hasDuplicateEmail) {
			throw new UserServiceException(UserAccountErrorCode.ERR_REGISTER_DUPLICATE_EMAIL);
		}

		// 3. 계정 식별자 생성
		String uuid = RandomStringUtils.randomAlphanumeric(13);

		// 4. 기본 계정 정보 생성
		String name = registerRequest.getLastName() + registerRequest.getFirstName();
		User user = User.builder()
			.email(registerRequest.getEmail())
			.password(passwordEncoder.encode(registerRequest.getPassword()))
			.name(name)
			.firstName(registerRequest.getFirstName())
			.lastName(registerRequest.getLastName())
			.birth(registerRequest.getBirth())
			.joinInfo(registerRequest.getJoinInfo())
			.serviceInfo(registerRequest.getServiceInfo())
			.marketInfoReceive(registerRequest.getMarketInfoReceive())
			.language(Language.KO)
			.userType(UserType.USER)
			.uuid(uuid)
			.build();

		// 5. 계정 추가정보 - 프로필 이미지 정보
		if (registerRequest.getProfile() != null) {
			try {
				String profile = fileService.upload(registerRequest.getProfile());
				user.setProfile(profile);
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		} else {
			user.setProfile(Default.USER_PROFILE.getValue());
		}

		// 6. 계정 추가정보 - 닉네임 정보
		if (!StringUtils.hasText(registerRequest.getNickname())) {
			user.setNickname(name);
		} else {
			user.setNickname(registerRequest.getNickname());
		}

		// 7.  계정 추가정보 - 연락처 정보
		if (StringUtils.hasText(registerRequest.getMobile())) {
			if (!registerRequest.getMobile().matches("^\\+\\d+-\\d{10,}$")) {
				log.error("휴대전화번호 형식 불일치(^\\+\\d+-\\d{10,}$) : [{}]", registerRequest.getMobile());
				throw new UserServiceException(UserAccountErrorCode.ERR_INVALID_REQUEST_PARAMETER);
			}
			String internationalNumber = registerRequest.getMobile().split("-")[0];
			user.setInternationalNumber(internationalNumber);
			//전화번호 추출(국제번호+ '-' 제외)
			String mobileNumber = registerRequest.getMobile().substring(internationalNumber.length() + 1);
			user.setMobile(mobileNumber);
		}

		// 8.  계정 추가정보 - 복구 이메일 정보
		if (StringUtils.hasText(registerRequest.getRecoveryEmail())) {
			user.setRecoveryEmail(registerRequest.getRecoveryEmail());
		}

		userRepository.save(user);

		// 회원가입 시 한달 무료 이벤트 쿠폰 등록
		payApiService.eventCouponRegisterToNewUser(user.getEmail(), user.getName(), user.getId());

		return convertUserEntityToUserInfoDto(user);
	}

	/**
	 * 사용자 엔티티 객체를 사용자 데이터 객체로 변환
	 *
	 * @param user - 사용제 엔티티 객체
	 * @return - 사용자 데이터 객체
	 */
	@Override
	public UserInfoResponse convertUserEntityToUserInfoDto(User user) {
		UserInfoResponse userInfoResponse = modelMapper.map(user, UserInfoResponse.class);
		if (StringUtils.isEmpty(user.getInternationalNumber()) || StringUtils.isEmpty(user.getMobile())) {
			userInfoResponse.setMobile("-");
		} else {
			userInfoResponse.setMobile(user.getInternationalNumber() + "-" + user.getMobile());
		}
		userinfoValueVerification(userInfoResponse);
		return userInfoResponse;
	}

	/**
	 * null 값을 빈 문자열로 치환
	 *
	 * @param userInfoResponse - 사용자 정보
	 */
	private void userinfoValueVerification(UserInfoResponse userInfoResponse) {
		if (userInfoResponse.getMobile().equals("-")) {
			userInfoResponse.setMobile("");
		}
		if (!StringUtils.hasText(userInfoResponse.getNickname())) {
			userInfoResponse.setNickname("");
		}
		if (!StringUtils.hasText(userInfoResponse.getRecoveryEmail())) {
			userInfoResponse.setRecoveryEmail("");
		}
		if (!StringUtils.hasText(userInfoResponse.getDescription())) {
			userInfoResponse.setDescription("");
		}
		if (!StringUtils.hasText(userInfoResponse.getQuestion())) {
			userInfoResponse.setQuestion("");
		}
		if (!StringUtils.hasText(userInfoResponse.getAnswer())) {
			userInfoResponse.setAnswer("");
		}
	}

	/**
	 * 사용자 목록 조회 요청 처리
	 *
	 * @param search   - 조회 조건
	 * @param pageable - 페이징 요청 정보
	 * @return - 사용자 정보 목록
	 */
	@Override
	public UserInfoListResponse getUserInfoList(String search, boolean paging, Pageable pageable) {
		if (!paging) {
			List<User> userList;
			if (search == null) {
				userList = userRepository.findAll();
			} else {
				userList = userRepository.findByNameIsContainingOrEmailIsContaining(search, search);
			}
			List<UserInfoResponse> userInfoList = userList.stream()
				.map(user -> modelMapper.map(user, UserInfoResponse.class))
				.collect(Collectors.toList());
			return new UserInfoListResponse(userInfoList, null);
		} else {
			Page<User> userPage;

			if (search == null) {
				userPage = userRepository.findAll(pageable);
			} else {
				userPage = userRepository.findByNameIsContainingOrEmailIsContaining(search, search, pageable);
			}

			List<UserInfoResponse> userInfoList = userPage.stream()
				.map(user -> modelMapper.map(user, UserInfoResponse.class))
				.collect(Collectors.toList());

			// Page metadata
			PageMetadataResponse pageMeta = PageMetadataResponse.builder()
				.currentPage(pageable.getPageNumber())
				.currentSize(pageable.getPageSize())
				.totalPage(userPage.getTotalPages())
				.totalElements(userPage.getTotalElements())
				.build();

			userInfoList.forEach(info -> log.info("{}", info));
			log.info("Paging Metadata: {}", pageMeta.toString());

			return new UserInfoListResponse(userInfoList, pageMeta);
		}
	}

	/**
	 * 이메일로 초대 사용자 정보 조회 요청 처리
	 *
	 * @param email - 초대될 사용자들의 이메일 정보 리스트
	 * @return - 초대될 사용자의 정보 리스트
	 */
	@Override
	public InviteUserInfoResponse getInviteUserInfoList(String email) {
		Optional<User> inviteUser = userRepository.findByEmail(email);
		if (inviteUser.isPresent()) {
			User user = inviteUser.get();
			InviteUserDetailInfoResponse inviteUserDetailInfoResponse = new InviteUserDetailInfoResponse(
				user.getUuid(), user.getEmail(), user.getName(), user.getFirstName(),
				user.getLastName(), user.getNickname()
			);
			return new InviteUserInfoResponse(true, inviteUserDetailInfoResponse);
		}
		return new InviteUserInfoResponse(false, InviteUserDetailInfoResponse.getDummy());
	}

	/**
	 * 사용자 상세 정보 조회 요청 처리
	 * @param request - 사용자 상세 정보 조회 요청 정보
	 * @return - 사용자 상세 정보
	 */
	@Override
	public UserDetailsInfoResponse getUserDetailsInfo(HttpServletRequest request) {
		String userUUID;
		if (request.getSession().getAttribute("userName") != null) {
			log.info("세션 정보 가져오기 성공");
			SecurityContextImpl securityContext = (SecurityContextImpl)request.getSession()
				.getAttribute("SPRING_SECURITY_CONTEXT");
			UserDetailsImpl userDetails = (UserDetailsImpl)securityContext.getAuthentication().getPrincipal();
			log.info("userDetails: {}", userDetails);
			userUUID = (String)request.getSession().getAttribute("userUUID");
		} else if (
			jwtTokenProvider.getJwtFromRequest(request) == null &&
				request.getHeader("service").equals("auth-server") &&
				StringUtils.hasText(request.getHeader("x-auth-uuid"))
		) {
			userUUID = request.getHeader("x-auth-uuid");
		} else {
			String authorizationToken = jwtTokenProvider.getJwtFromRequest(request);

			if (!jwtTokenProvider.isValidToken(authorizationToken)) {
				throw new UserServiceException(UserAccountErrorCode.ERR_API_AUTHENTICATION);
			}

			JwtPayload jwtPayload = jwtTokenProvider.getJwtPayload(authorizationToken);
			log.info("JWT PAYLOAD: {}", jwtPayload);
			log.info("USER UUID: {}", jwtPayload.getUuid());
			userUUID = jwtPayload.getUuid();
		}

		UserInfoResponse userInfoResponse = getUserInfoByUUID(userUUID);
		ApiResponse<WorkspaceInfoListResponse> responseMessage = workspaceRestService.getMyWorkspaceInfoList(
			userUUID, 50);

		return new UserDetailsInfoResponse(userInfoResponse, responseMessage.getData().getWorkspaceList());
	}

	/**
	 * 사용자 정보 수정 요청 처리
	 * @param userUUID - 사용자 정보 식별자
	 * @param userInfoModifyRequest - 사용자 정보 수정 요청
	 * @return - 수정된 사용자 정보
	 */
	@Override
	@Transactional
	public UserInfoResponse modifyUserInfo(String userUUID, UserInfoModifyRequest userInfoModifyRequest) {
		User user = userRepository.findByUuid(userUUID)
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND));

		if (userInfoModifyRequest.getFirstName() != null) {
			user.setFirstName(userInfoModifyRequest.getFirstName());
			user.setName(user.getLastName() + user.getFirstName());
		}

		if (userInfoModifyRequest.getLastName() != null) {
			user.setLastName(userInfoModifyRequest.getLastName());
			user.setName(user.getLastName() + user.getFirstName());
		}

		if (userInfoModifyRequest.getNickname() != null) {
			user.setNickname(userInfoModifyRequest.getNickname());
		}

		if (userInfoModifyRequest.getPassword() != null) {
			// Check for duplicated password with previous password
			if (passwordEncoder.matches(userInfoModifyRequest.getPassword(), user.getPassword())) {
				throw new UserServiceException(UserAccountErrorCode.ERR_USER_PASSWORD_CHANGE_DUPLICATE);
			}

			String encodedPassword = passwordEncoder.encode(userInfoModifyRequest.getPassword());
			user.setPassword(encodedPassword);
			user.setPasswordUpdateDate(LocalDateTime.now());

			// 비밀번호 초기화 여부 수정
			if (!user.isAccountPasswordInitialized()) {
				user.setAccountPasswordInitialized(true);
			}
		}

		if (userInfoModifyRequest.getMobile() != null) {
			if (!userInfoModifyRequest.getMobile().matches("^\\+\\d+-\\d{10,}$")) {
				log.error("Register Request mobile parameter format is invalid: {}", userInfoModifyRequest.getMobile());
				throw new UserServiceException(UserAccountErrorCode.ERR_INVALID_REQUEST_PARAMETER);
			}
			String internationalNumber = userInfoModifyRequest.getMobile().split("-")[0];
			user.setInternationalNumber(internationalNumber);
			//전화번호 추출(국제번호+ '-' 제외)
			String mobileNumber = userInfoModifyRequest.getMobile().substring(internationalNumber.length() + 1);
			user.setMobile(mobileNumber);
		}

		if (userInfoModifyRequest.getBirth() != null) {
			user.setBirth(LocalDate.parse(userInfoModifyRequest.getBirth(), DateTimeFormatter.ISO_DATE));
		}

		if (userInfoModifyRequest.getRecoveryEmail() != null) {
			user.setRecoveryEmail(userInfoModifyRequest.getRecoveryEmail());
		}

		if (userInfoModifyRequest.getMarketInfoReceive() != null) {
			user.setMarketInfoReceive(Status.valueOf(userInfoModifyRequest.getMarketInfoReceive()));
		}

		if (userInfoModifyRequest.getQuestion() != null) {
			user.setQuestion(userInfoModifyRequest.getQuestion());
			// 비밀번호 초기화 여부 수정
			if (!user.isAccountPasswordInitialized()) {
				user.setAccountPasswordInitialized(true);
			}
		}

		if (userInfoModifyRequest.getAnswer() != null) {
			user.setAnswer(userInfoModifyRequest.getAnswer());
			// 비밀번호 초기화 여부 수정
			if (!user.isAccountPasswordInitialized()) {
				user.setAccountPasswordInitialized(true);
			}
		}

		userRepository.save(user);

		return convertUserEntityToUserInfoDto(user);
	}

	/**
	 * 사용자 정보 접근 권한 여부 체크
	 *
	 * @param userUUID                   - 사용자 식별 번호
	 * @param userInfoAccessCheckRequest - 접근 요청 데이터(사용자 아이디 및 비밀번호 정보)
	 * @return - 접근 권한 체크 여부 및 사용자 정보 데이터
	 */
	@Override
	public UserInfoAccessCheckResponse userInfoAccessCheck(
		String userUUID, UserInfoAccessCheckRequest userInfoAccessCheckRequest
	) {
		User user = userRepository.findByEmail(userInfoAccessCheckRequest.getEmail())
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_INFO_ACCESS));

		if (!user.getUuid().equals(userUUID)) {
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_INFO_ACCESS);
		}

		if (!passwordEncoder.matches(userInfoAccessCheckRequest.getPassword(), user.getPassword())) {
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_INFO_ACCESS);
		}

		UserInfoResponse userInfoResponse = convertUserEntityToUserInfoDto(user);
		UserInfoAccessCheckResponse userInfoAccessCheckResponse = new UserInfoAccessCheckResponse();
		userInfoAccessCheckResponse.setAccessCheckResult(true);
		userInfoAccessCheckResponse.setUserInfo(userInfoResponse);
		return userInfoAccessCheckResponse;
	}

	/**
	 * 사용자 프로필 업데이트 요청 처리
	 * @param userUUID - 사용자 식별자
	 * @param userProfileUpdateRequest - 프로필 이미지 변경 요청 정보
	 * @return - 프로필 이미지 변경된 사용자 정보
	 */
	@Override
	@Transactional
	public UserProfileUpdateResponse profileImageUpdate(
		String userUUID, UserProfileUpdateRequest userProfileUpdateRequest
	) {
		User user = userRepository.findByUuid(userUUID)
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_PROFILE_IMAGE_UPLOAD));

		// if new profile image request
		if (userProfileUpdateRequest.getProfile() != null) {
			try {
				String profileUrl = fileService.upload(userProfileUpdateRequest.getProfile());
				fileService.delete(user.getProfile());
				user.setProfile(profileUrl);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
				throw new UserServiceException(UserAccountErrorCode.ERR_USER_PROFILE_IMAGE_UPLOAD);
			}
		} else { // if set as default image
			if (!user.getProfile().equals("default")) {
				user.setProfile(Default.USER_PROFILE.getValue());
			}
		}

		userRepository.save(user);
		return new UserProfileUpdateResponse(user.getUuid(), user.getProfile());
	}

	/**
	 * 회원가입 상세 정보 등록 요청 처리
	 * @param registerDetailsRequest - 사용자 회원가입 상세 정보
	 * @return - 등록 회원 정보
	 */
	@Override
	@Transactional
	public UserInfoResponse registerWithDetails(RegisterDetailsRequest registerDetailsRequest) {
		User user = userRepository.findByUuid(registerDetailsRequest.getUuid())
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND));

		if (registerDetailsRequest.getNickname() != null) {
			user.setNickname(registerDetailsRequest.getNickname());
		}

		if (registerDetailsRequest.getMobile() != null) {
			user.setMobile(registerDetailsRequest.getMobile());
		}

		if (registerDetailsRequest.getRecoveryEmail() != null) {
			user.setRecoveryEmail(registerDetailsRequest.getRecoveryEmail());
		}

		if (registerDetailsRequest.getProfile() != null) {
			String profileUrl;
			try {
				profileUrl = fileService.upload(registerDetailsRequest.getProfile());
			} catch (IOException e) {
				log.error(e.getMessage(), e);
				throw new UserServiceException(UserAccountErrorCode.ERR_USER_PROFILE_IMAGE_UPLOAD);
			}
			user.setProfile(profileUrl);
		}

		userRepository.save(user);

		return convertUserEntityToUserInfoDto(user);
	}

	/**
	 * 사용자 이메일 아이디 찾기 요청 처리
	 * @param userEmailFindRequest - 이메일 찾기 요청 정보
	 * @return - 이메일 찾기 결과 정보
	 */
	@Override
	@Transactional(readOnly = true)
	public UserEmailFindResponse userFindEmailHandler(UserEmailFindRequest userEmailFindRequest) {
		String name = userEmailFindRequest.getLastName() + userEmailFindRequest.getFirstName();
		List<User> userList = userRepository.findUserByNameAndRecoveryEmailOrInternationalNumberAndMobile(
			name, userEmailFindRequest.getRecoveryEmail(), userEmailFindRequest.getMobile());

		if (userList.isEmpty()) {
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND);
		}

		UserEmailFindResponse userEmailFindResponse = new UserEmailFindResponse();
		List<UserEmailFindInfoResponse> userEmailFindInfoResponses = new ArrayList<>();
		for (User user : userList) {
			UserEmailFindInfoResponse userEmailFindInfoResponse = new UserEmailFindInfoResponse();
			String maskedEmail = emailMaskingProcess(user.getEmail(), 4);
			userEmailFindInfoResponse.setEmail(maskedEmail);
			userEmailFindInfoResponse.setSignUpDate(user.getCreatedDate().toLocalDate());
			userEmailFindInfoResponses.add(userEmailFindInfoResponse);
		}
		userEmailFindResponse.setEmailFindInfoList(userEmailFindInfoResponses);
		return userEmailFindResponse;
	}

	/**
	 * 사용자 정보 전체 조회 (어드민 서비스)
	 * @param pageable - 페이징 요청 정보
	 * @return - 사용자 정보 리스트
	 */
	@Override
	public UserInfoListResponse findAllUserInfo(Pageable pageable) {
		Page<User> userPage = userRepository.findAll(pageable);
		List<UserInfoResponse> userInfoResponseList = userPage.stream()
			.map(user -> modelMapper.map(user, UserInfoResponse.class))
			.collect(Collectors.toList());

		PageMetadataResponse pageMeta = PageMetadataResponse.builder()
			.currentPage(pageable.getPageNumber())
			.currentSize(pageable.getPageSize())
			.totalPage(userPage.getTotalPages())
			.totalElements(userPage.getTotalElements())
			.build();

		return new UserInfoListResponse(userInfoResponseList, pageMeta);
	}

	/**
	 * 이메일 마스킹 처리
	 *
	 * @param email              - 이메일 정보
	 * @param ignoreLetterLength - 마스킹 문자열 수
	 * @return - 마스킹처리된 이메일 정보
	 */
	private String emailMaskingProcess(String email, int ignoreLetterLength) {
		StringBuilder sb = new StringBuilder();
		String[] emailDivideByAtSign = email.split("@");
		String emailID = emailDivideByAtSign[0];
		String emailDomain = emailDivideByAtSign[1];

		int emailIDMaskingLength = emailID.length() > ignoreLetterLength ? ignoreLetterLength : emailID.length() / 2;
		int emailDomainMaskingLength =
			emailDomain.length() > ignoreLetterLength ? ignoreLetterLength : emailDomain.length() / 2;

		sb.append(emailIDMaskingProcess(emailID, emailIDMaskingLength));
		sb.append("@");
		sb.append(emailDomainMaskingProcess(emailDomain, emailDomainMaskingLength));
		return sb.toString();
	}

	/**
	 * 이메일 아이디 부분 마스킹 처리
	 *
	 * @param emailId       - 이메일 아이디 부분 문자열
	 * @param maskingLength - 이메일 아이디 마스킹 문자열 수
	 * @return - 마스킹 처리된 이메일 아이디 문자열
	 */
	private String emailIDMaskingProcess(String emailId, int maskingLength) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < emailId.length(); i++) {
			if (i < maskingLength) {
				sb.append(emailId.charAt(i));
			} else {
				sb.append("*");
			}
		}
		return sb.toString();
	}

	/**
	 * 이메일 도메인 부분 마스킹 처리
	 *
	 * @param emailDomain   - 이메일 도메인 부분 문자열
	 * @param maskingLength - 이메일 도메인 마스킹 문자열 수
	 * @return - 마스킹 처리된 이메일 도메인 문자열
	 */
	private String emailDomainMaskingProcess(String emailDomain, int maskingLength) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < emailDomain.length(); i++) {
			if (i < maskingLength) {
				sb.append(emailDomain.charAt(i));
			} else {
				sb.append("*");
			}
		}
		return sb.toString();
	}

	/**
	 * 비밀번호 변경 인증 코드 메일 전송 처리
	 * @param passwordAuthCodeRequest - 비밀번호 변경 요청 관련 정보
	 * @param locale
	 * @return - 인증 메일 전송 결과
	 */
	@Override
	@Transactional
	public UserPasswordFindAuthCodeResponse userPasswordFindAuthCodeSendHandler(
		UserPasswordFindAuthCodeRequest passwordAuthCodeRequest,
		Locale locale
	) {
		User user = userRepository.findByEmail(passwordAuthCodeRequest.getEmail())
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND));

		boolean hasPreviousAuthenticationInfo = userPasswordAuthCodeRepository.existsById(user.getEmail());

		if (hasPreviousAuthenticationInfo) {
			userPasswordAuthCodeRepository.deleteById(user.getEmail());
		}

		String authCode = RandomStringUtils.randomNumeric(6);
		Duration expiredDuration = Duration.ofMinutes(1);

		PasswordInitAuthCode passwordInitAuthCode = PasswordInitAuthCode.builder()
			.email(user.getEmail())
			.uuid(user.getUuid())
			.name(user.getName())
			.code(authCode)
			.expiredDate(expiredDuration.getSeconds())
			.build();
		userPasswordAuthCodeRepository.save(passwordInitAuthCode);

		ZoneOffset seoulZoneOffset = ZoneOffset.of("+09:00");
		ZonedDateTime zonedDateTime = ZonedDateTime.now(seoulZoneOffset).plusSeconds(expiredDuration.getSeconds());

		Context context = new Context();
		context.setVariable("code", authCode);
		context.setVariable("expiredDate", zonedDateTime);

		String mailTemplatePath = String.format("%s/password_change_code", locale.getLanguage());
		log.info("mailTemplatePath: {}", mailTemplatePath);
		String mailTitle = messageSource.getMessage("MAIL_TITLE_OF_PASSWORD_CHANGE_AUTH_CODE", null, locale);
		String message = templateEngine.process(mailTemplatePath, context);

		EmailMessage passwordInitializeCode = new EmailMessage();
		passwordInitializeCode.setSubject(mailTitle);
		passwordInitializeCode.setTo(user.getEmail());
		passwordInitializeCode.setMessage(message);

		emailService.sendEmail(passwordInitializeCode);

		log.info(
			"[SEND_REGISTRATION_SUCCESS_MAIL] - title: {} , to: {}",
			passwordInitializeCode.getSubject(),
			passwordInitializeCode.getTo()
		);

		return new UserPasswordFindAuthCodeResponse(true, user.getEmail());
	}

	/**
	 * 사용자 비밀번호 변경 인증 코드 정보 확인 처리
	 * @param authCodeCheckRequest - 비밀번호 변경 요청 인증 정보
	 * @return - 비밀번호 변경 요청 인증 정보 검증 결과
	 */
	@Override
	@Transactional
	public UserPasswordFindCodeCheckResponse userPasswordFindAuthCodeCheckHandler(
		UserPasswordFindAuthCodeCheckRequest authCodeCheckRequest
	) {
		PasswordInitAuthCode passwordInitAuthCode = userPasswordAuthCodeRepository.findById(
			authCodeCheckRequest.getEmail())
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_PASSWORD_INIT_CODE_NOT_FOUND));

		if (!passwordInitAuthCode.getCode().equals(authCodeCheckRequest.getCode())) {
			throw new UserServiceException(UserAccountErrorCode.ERR_PASSWORD_INIT_CODE_NOT_FOUND);
		}

		log.info("Password Initialize Info Check : [{}]", passwordInitAuthCode);

		userPasswordAuthCodeRepository.deleteById(authCodeCheckRequest.getEmail());

		return new UserPasswordFindCodeCheckResponse(
			passwordInitAuthCode.getUuid(), passwordInitAuthCode.getEmail(), true);
	}

	/**
	 * 사용자 비밀번호 변경 처리
	 * @param passwordChangeRequest - 비밀번호 변경 요청 정보
	 * @return - 비밀번호 변경 결과 정보
	 */
	@Override
	@Transactional
	public UserPasswordChangeResponse userPasswordChangeHandler(UserPasswordChangeRequest passwordChangeRequest) {
		User user = userRepository.findByUuid(passwordChangeRequest.getUuid())
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_PASSWORD_CHANGE));

		// Check for duplicated password with previous password
		if (passwordEncoder.matches(passwordChangeRequest.getPassword(), user.getPassword())) {
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_PASSWORD_CHANGE_DUPLICATE);
		}

		String encodedNewPassword = passwordEncoder.encode(passwordChangeRequest.getPassword());
		user.setPassword(encodedNewPassword);
		user.setPasswordUpdateDate(LocalDateTime.now());

		// password invalid account lock free
		if (!user.isAccountNonLocked()) {
			log.info(
				"[USER][INACTIVE_ACCOUNT_LOCK] - Email:[{}] Name:[{}]", user.getEmail(),
				user.getName()
			);
			user.setAccountNonLocked(true);
		}
		userRepository.save(user);
		return new UserPasswordChangeResponse(true, user.getEmail(), user.getPasswordUpdateDate());
	}

	/**
	 * 사용자 정보 검색
	 * @param search - 검색어 (닉네임 또는 이메일)
	 * @param workspaceUserIdList - 사용자 식별번호 리스트 정보
	 * @return - 사용자 정보리스트
	 */
	@Override
	public UserInfoListResponse getUsersInfoList(String search, String[] workspaceUserIdList) {
		List<UserInfoResponse> userInfoList = new ArrayList<>();

		for (String uuid : workspaceUserIdList) {
			Optional<SecessionUser> secessionUser = secessionUserRepository.findByUserUUID(uuid);
			if (StringUtils.hasText(search)) {
				List<User> userList = userRepository.findByNicknameIsContainingOrEmailIsContaining(search, search);
				// 탈퇴 회원 검사
				if (secessionUser.isPresent() &&
					(secessionUser.get().getNickName().contains(search) ||
						secessionUser.get().getEmail().contains(search))
				) {
					UserInfoResponse secessionUserInfoResponse = new UserInfoResponse();
					secessionUserInfoResponse.setSecessionUserInfo(secessionUser.get());
					userInfoList.add(secessionUserInfoResponse);
					continue;
				}

				User user = userRepository.findByUuid(uuid).orElse(null);
				if (userList.contains(user)) {
					UserInfoResponse userInfoResponse = modelMapper.map(user, UserInfoResponse.class);
					userInfoList.add(userInfoResponse);
				}

			} else {
				if (secessionUser.isPresent()) {
					UserInfoResponse secessionUserInfoResponse = new UserInfoResponse();
					secessionUserInfoResponse.setSecessionUserInfo(secessionUser.get());
					userInfoList.add(secessionUserInfoResponse);
					continue;
				}

				User user = userRepository.findByUuid(uuid).orElse(null);
				if (user != null) {
					UserInfoResponse userInfoResponse = modelMapper.map(user, UserInfoResponse.class);
					userInfoList.add(userInfoResponse);
				}
				// UserInfoResponse userInfoResponse = getProxyBean().getUserInfoByUserId(uuid);
				// userInfoList.add(userInfoResponse);
			}
		}

		return new UserInfoListResponse(userInfoList, null);
	}

	/**
	 * 사용자 서비스 접속 기록 조회
	 * @param userId - 사용자 식별자 정보
	 * @param pageable - 페이징 요청 정보
	 * @return - 사용자 서비스 접속 기록 정보
	 */
	@Override
	public UserAccessHistoryResponse getUserAccessDeviceHistory(String userId, Pageable pageable) {
		User user = userRepository.findByUuid(userId)
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND));
		Page<UserAccessLog> deviceMetadataPage = userAccessLogRepository.findAllByUser(user, pageable);

		List<UserAccessDeviceInfoResponse> accessDeviceInfoList = deviceMetadataPage
			.stream().map(userAccessLog -> {
				UserAccessDeviceInfoResponse accessDeviceInfo = modelMapper.map(
					userAccessLog, UserAccessDeviceInfoResponse.class);
				String majorDeviceName = accessDeviceInfo.getDevice().split(" ")[0];
				majorDeviceName = majorDeviceName.equals("okhttp") ? "Mobile" : majorDeviceName;
				accessDeviceInfo.setDevice(majorDeviceName);
				return accessDeviceInfo;
			})
			.collect(Collectors.toList());

		// Page metadata
		PageMetadataResponse pageMeta = PageMetadataResponse.builder()
			.currentPage(pageable.getPageNumber())
			.currentSize(pageable.getPageSize())
			.totalPage(deviceMetadataPage.getTotalPages())
			.totalElements(deviceMetadataPage.getTotalElements())
			.build();

		return new UserAccessHistoryResponse(accessDeviceInfoList, pageMeta);
	}

	/**
	 * 회원탈퇴 처리
	 *
	 * @param userSecessionRequest - 회원탈퇴 요청
	 * @return - 회원 탈퇴 처리 결과
	 */
	@Override
	@Transactional
	public UserSecessionResponse userSecessionRequestHandler(UserSecessionRequest userSecessionRequest) {
		User user = userRepository.findByEmailAndUuid(userSecessionRequest.getEmail(), userSecessionRequest.getUuid())
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND));

		if (!passwordEncoder.matches(userSecessionRequest.getPassword(), user.getPassword())) {
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_SECESSION_PASSWORD);
		}

		// 1. 탈퇴 계정에 등록
		registerUserToSecessionUser(user, userSecessionRequest.getReason());

		// 2. 계정 관련 정보 삭제
		deleteAllRelatedUserInfo(user);

		// 3. 계정 삭제
		userRepository.delete(user);

		WorkspaceInfoResponse userMasterWorkspaceInfo = getWorkspaceInfoResponseByUserId(user.getUuid());

		if (userMasterWorkspaceInfo == null) {
			return new UserSecessionResponse(user.getEmail(), user.getName(), LocalDateTime.now());
		}

		// 1. workspace delete
		ApiResponse<WorkspaceSecessionResponse> workspaceResponse = workspaceRestService.workspaceSecession(
			"user-server", userMasterWorkspaceInfo.getUuid()
		);

		if (workspaceResponse == null || !workspaceResponse.getData().isResult()) {
			log.error("[WORKSPACE_SECESSION ERROR]");
			// throw new UserServiceException(ErrorCode.ERR_USER_SECESSION);
		}

		// 2. content delete
		ApiResponse<ContentSecessionResponse> contentResponse = contentRestService.contentSecession(
			"user-server", userMasterWorkspaceInfo.getUuid()
		);
		if (contentResponse == null || !contentResponse.getData().isResult()) {
			log.error("[CONTENT_SECESSION ERROR]");
			// throw new UserServiceException(ErrorCode.ERR_USER_SECESSION);
		}

		// 3. task delete
		ApiResponse<TaskSecessionResponse> taskResponse = processRestService.taskSecession(
			"user-server", userMasterWorkspaceInfo.getUuid(), user.getUuid()
		);
		if (taskResponse == null || !taskResponse.getData().isResult()) {
			log.error("[TASK_SECESSION ERROR]");
			// throw new UserServiceException(ErrorCode.ERR_USER_SECESSION);
		}

		// 4. remote service delete
		ApiResponse<RemoteSecessionResponse> remoteResponse = remoteRestService.remoteUserSecession(user.getUuid());
		if (remoteResponse == null || !remoteResponse.getData().isResult()) {
			log.error("[REMOTE_SECESSION ERROR]");
		}

		// 5. license inactive
		ApiResponse<LicenseSecessionResponse> licenseResponse = licenseRestService.licenseSecession(
			"user-server", userMasterWorkspaceInfo.getUuid(), user.getUuid(), user.getId()
		);
		if (licenseResponse == null || !licenseResponse.getData().isResult()) {
			log.error("[License_SECESSION ERROR]");
			// throw new UserServiceException(ErrorCode.ERR_USER_SECESSION);
		}
		return new UserSecessionResponse(user.getEmail(), user.getName(), LocalDateTime.now());
	}

	/**
	 * 탈퇴 계정 정보에 탈퇴 대상 계정 정보 등록
	 * @param user - 탈퇴 대상 계정 정보
	 * @param secessionReason - 탈퇴 사유
	 */
	private void registerUserToSecessionUser(final User user, final String secessionReason) {
		LocalDateTime secessionDate = LocalDateTime.now();
		long periodMonth = Period.between(user.getCreatedDate().toLocalDate(), secessionDate.toLocalDate()).getMonths();
		long periodDays = Period.between(user.getCreatedDate().toLocalDate(), secessionDate.toLocalDate()).getDays();
		long serviceDays = (periodMonth == 0 ? 1 : periodMonth) * periodDays;
		SecessionUser secessionUser = SecessionUser.builder()
			.email(user.getEmail())
			.userUUID(user.getUuid())
			.userId(user.getId())
			.nickName(user.getNickname() == null ? user.getName() : user.getNickname())
			.joinDate(user.getCreatedDate())
			.joinInfo(user.getJoinInfo())
			.serviceInfo(user.getServiceInfo())
			.reason(secessionReason)
			.serviceDay(serviceDays)
			.secessionDate(secessionDate)
			.build();
		secessionUserRepository.save(secessionUser);
		log.info("[USER_SECESSION] - {}", secessionUser.toString());
	}

	/**
	 * 계정 관련 정보 삭제 (권한, otp, 접속 로그)
	 * @param user - 삭제 대상 계정 정보
	 */
	private void deleteAllRelatedUserInfo(User user) {
		// 계정 권한 삭제
		userPermissionRepository.deleteAllUserPermissionByUser(user);
		// otp 정보 삭제
		userOTPRepository.deleteAllByEmail(user.getEmail());
		// 접속 로그 삭제
		userAccessLogRepository.deleteAllUserAccessLogByUser(user);
	}

	/**
	 * 마스터 워크스페이스 정보 추출
	 * @param userUUID - 사용자 고유 식별자
	 * @return - 마스터 워크스페이스 정보
	 */
	private WorkspaceInfoResponse getWorkspaceInfoResponseByUserId(String userUUID) {
		ApiResponse<WorkspaceInfoListResponse> workspaceApiResponse = workspaceRestService.getMyWorkspaceInfoList(
			userUUID, 50
		);
		if (workspaceApiResponse.getCode() != 200 ||
			workspaceApiResponse.getData().getWorkspaceList().isEmpty()
		) {
			log.error("[WORKSPACE_INFO_NOT_FOUND] - {}", workspaceApiResponse.getMessage());
			return null;
		}

		for (WorkspaceInfoResponse myWorkspaceInfo : workspaceApiResponse.getData().getWorkspaceList()) {
			if (myWorkspaceInfo.getRole().equals("MASTER")) {
				return myWorkspaceInfo;
			}
		}
		return null;
	}

	/**
	 * 멤버 추가 처리
	 * @param registerMemberRequest - 새로 추가할 멤버 사용자 정보
	 * @return - 새로 등록된 멤버 사용자 정보
	 */
	@Override
	@Transactional
	public UserInfoResponse registerNewMember(RegisterMemberRequest registerMemberRequest) {
		User user = User.builder()
			.uuid(RandomStringUtils.randomAlphanumeric(13))
			.email(registerMemberRequest.getEmail())
			.password(passwordEncoder.encode(registerMemberRequest.getPassword()))
			.lastName(registerMemberRequest.getEmail())
			.firstName("-Member")
			.name(registerMemberRequest.getEmail() + "-Member")
			.nickname(registerMemberRequest.getEmail() + "-Member")
			.profile(Default.USER_PROFILE.getValue())
			.userType(UserType.MEMBER_USER)
			.birth(LocalDate.now())
			.joinInfo("워크스페이스 멤버 등록")
			.serviceInfo("워크스페이스 멤버 등록")
			.language(Language.KO)
			.marketInfoReceive(Status.REJECT)
			.build();

		userRepository.save(user);

		log.info("[REGISTER_NEW_MEMBER_USER] - {}", user.toString());

		return convertUserEntityToUserInfoDto(user);
	}

	/**
	 *
	 * @param userUUID - 삭제할 멤버 사용자의 고유 식별자
	 * @return - 삭제 처리 결과
	 */
	@Override
	@Transactional
	public UserDeleteResponse deleteMemberUser(String userUUID) {
		User deleteUser = userRepository.findByUuidAndUserType(userUUID, UserType.MEMBER_USER)
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND));

		userAccessLogRepository.deleteAllUserAccessLogByUser(deleteUser);
		userRepository.delete(deleteUser);

		// remote service delete
		ApiResponse<RemoteSecessionResponse> remoteResponse = remoteRestService.remoteUserSecession(userUUID);
		if (remoteResponse == null || !remoteResponse.getData().isResult()) {
			log.error("[REMOTE_MEMBER_USER_SECESSION ERROR]");
		}

		UserDeleteResponse userDeleteResponse = new UserDeleteResponse();
		userDeleteResponse.setUserUUID(userUUID);
		userDeleteResponse.setDeletedDate(LocalDateTime.now());
		return userDeleteResponse;
	}

	/**
	 *  이메일 존재 여부 검사
	 * @param email - 존재 여부 검사 대상 이메일
	 * @return - 존재여부
	 */
	@Override
	@Transactional(readOnly = true)
	public UserEmailExistCheckResponse userEmailExistCheck(String email) {
		Optional<User> user = userRepository.findByEmail(email);

		// 계정이 없는 경우
		if (!user.isPresent()) {
			return new UserEmailExistCheckResponse(email, false, LocalDateTime.now());
		}

		//  비밀번호 찾기 질의 정보가 설정되어있지 않은 경우
		if (StringUtils.isEmpty(user.get().getQuestion()) && StringUtils.isEmpty(user.get().getAnswer())) {
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_PASSWORD_QUESTION_AND_ANSWER_NOT_INITIALIZED);
		}

		return new UserEmailExistCheckResponse(email, true, LocalDateTime.now());
	}

	/**
	 * 비밀번호 변경 질문 및 답변 확인
	 * @param userIdentityCheckRequest - 비밀번호 변경을 위한 사용자 인증 정보
	 * @return - 인증 결과 정보
	 */
	@Override
	@Transactional
	public UserIdentityCheckResponse userIdentityCheck(UserIdentityCheckRequest userIdentityCheckRequest) {
		User user = userRepository.findByEmail(userIdentityCheckRequest.getEmail())
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND));

		if (!user.getQuestion().equals(userIdentityCheckRequest.getQuestion()) || !user.getAnswer()
			.equals(userIdentityCheckRequest.getAnswer())
		) {
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_PASSWORD_CHANGE_ANSWER_AND_QUESTION);
		}
		return new UserIdentityCheckResponse(user.getEmail(), user.getUuid(), LocalDateTime.now());
	}

	/**
	 * 멤버 사용자 비밀번호 변경 처리
	 * @param memberUserPasswordChangeRequest - 비밀번호 변경 요청 정보
	 * @return - 비밀번호 변경 처리 결과
	 */
	@Override
	@Transactional
	public MemberUserPasswordChangeResponse memberUserPasswordChangeHandler(
		MemberUserPasswordChangeRequest memberUserPasswordChangeRequest
	) {
		User memberUser = userRepository.findByUuidAndUserType(
			memberUserPasswordChangeRequest.getUuid(), UserType.MEMBER_USER)
			.orElseThrow(() -> {
				log.error(
					"[MEMBER_USER_PASSWORD_CHANGE] - uuid:[{}], userType:[{}] NOT FOUND",
					memberUserPasswordChangeRequest.getUuid(), UserType.MEMBER_USER
				);
				return new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND);
			});
		memberUser.setAnswer(null);
		memberUser.setQuestion(null);
		memberUser.setAccountPasswordInitialized(false);
		memberUser.setPassword(passwordEncoder.encode(memberUserPasswordChangeRequest.getPassword()));
		memberUser.setPasswordUpdateDate(LocalDateTime.now());

		if (!memberUser.isAccountNonLocked()) {
			log.info(
				"[MEMBER_USER][INACTIVE_ACCOUNT_LOCK] - Email:[{}] Name:[{}]", memberUser.getEmail(),
				memberUser.getName()
			);
			memberUser.setAccountNonLocked(true);
		}

		userRepository.save(memberUser);

		MemberUserPasswordChangeResponse response = new MemberUserPasswordChangeResponse(
			true, memberUser.getEmail(), memberUser.getUuid(), LocalDateTime.now()
		);
		log.info("[MEMBER_USER_PASSWORD_CHANGE_RESPONSE] - {}", response.toString());
		return response;
	}

	/**
	 * 사용자 UUID 리스트 기반 사용자 정보 조회
	 * @param uuidList - 사용자 식별자 배열
	 * @return - 사용자 정보 배열
	 */
	@Override
	public UserInfoListOnlyResponse getUserInfoListByUUIDList(String[] uuidList) {
		List<UserInfoResponse> userInfoResponses = new ArrayList<>();
		for (String uuid : uuidList) {
			UserInfoResponse userInfoResponse = getProxyBean().getUserInfoByUserId(uuid);
			userInfoResponses.add(userInfoResponse);
		}
		return new UserInfoListOnlyResponse(userInfoResponses);
	}

	private UserServiceImpl getProxyBean() {
		return applicationContext.getBean(UserServiceImpl.class);
	}
}
