package com.virnect.uaa.domain.user.api;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

import com.virnect.uaa.domain.user.application.InternalUserService;
import com.virnect.uaa.domain.user.dto.response.UserInfoListResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoResponse;
import com.virnect.uaa.domain.user.error.UserAccountErrorCode;
import com.virnect.uaa.domain.user.exception.UserServiceException;
import com.virnect.uaa.global.common.ApiResponse;

@Api(value = "내부 서버 통신용 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class IntervalUserInformationController {
	private final InternalUserService internalUserService;

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
		UserInfoListResponse responseMessage = internalUserService.findAllUserInfo(pageable);
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
		UserInfoListResponse responseMessage = internalUserService.getUsersInfoList(search, workspaceUserIdList);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	@ApiOperation(value = "사용자 정보 조회", notes = "특정 사용자의 정보를 조회하는 api 입니다.", tags = "billing server only")
	@ApiImplicitParam(name = "userId", value = "사용자 대표 식별자", dataType = "string", type = "path", required = true, defaultValue = "1")
	@GetMapping("/billing/{userId}")
	public ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfoByUserIdFromBillingSystem(
		@PathVariable("userId") long userId
	) {
		if (userId <= 0) {
			throw new UserServiceException(UserAccountErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		UserInfoResponse responseMessage = internalUserService.getUserInfoByUserId(userId);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

}
