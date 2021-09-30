package com.virnect.uaa.domain.user.api;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

import com.virnect.uaa.domain.user.application.UserInformationRetrieveService;
import com.virnect.uaa.domain.user.dto.response.InviteUserInfoResponse;
import com.virnect.uaa.domain.user.dto.response.UserAccessHistoryResponse;
import com.virnect.uaa.domain.user.dto.response.UserDetailsInfoResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoListOnlyResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoListResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoResponse;
import com.virnect.uaa.domain.user.error.UserAccountErrorCode;
import com.virnect.uaa.domain.user.exception.UserServiceException;
import com.virnect.uaa.global.common.ApiResponse;
import com.virnect.uaa.global.common.PageRequest;

@Api(value = "사용자 정보 조회 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserInfoRetrieveController {
	private final UserInformationRetrieveService userInformationRetrieveService;

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
		UserInfoListResponse responseMessage = userInformationRetrieveService.searchUserInformation(
			search, pageable.of(), paging
		);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
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
		HttpServletRequest request, Authentication authentication
	) {
		UserDetailsInfoResponse responseMessage = userInformationRetrieveService.getUserDetailsInformationByUserAuthenticatedRequest(
			request, authentication);
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
		UserInfoResponse responseMessage = userInformationRetrieveService.findUserInformationByUserUUID(userId);
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
		UserAccessHistoryResponse responseMessage = userInformationRetrieveService.getUserAccessHistoryByUserUUID(
			userId, pageable.of());
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
		UserInfoListOnlyResponse userInfoListOnlyResponse = userInformationRetrieveService.getUserInformationListByUserUUIDArray(
			uuid);
		return ResponseEntity.ok(new ApiResponse<>(userInfoListOnlyResponse));
	}

	@ApiOperation(value = "이메일로 사용자 정보 조회", notes = "워크스페이스 초대를 위해 이메일을 통해 사용자 정보를 조호할 수 있는 api 입니다.")
	@GetMapping("/invite")
	public ResponseEntity<ApiResponse<InviteUserInfoResponse>> getInviteUserInfo(
		@RequestParam(value = "email", required = false) String email
	) {
		if (StringUtils.isEmpty(email)) {
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_INFO);
		}
		InviteUserInfoResponse responseMessage = userInformationRetrieveService.getUserInformationByUserEmail(email);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

}
