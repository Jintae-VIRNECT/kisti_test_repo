package com.virnect.uaa.infra.rest.user;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.dto.user.request.RegisterRequest;
import com.virnect.uaa.global.common.ApiResponse;
import com.virnect.uaa.infra.rest.user.dto.UserDetailsInfoResponse;
import com.virnect.uaa.infra.rest.user.dto.UserInfoResponse;

@Slf4j
@Service
public class UserFallbackFactory implements FallbackFactory<UserRestService> {
	@Override
	public UserRestService create(Throwable cause) {
		log.error(cause.getMessage(), cause);
		return new UserRestService() {
			@Override
			public ApiResponse<UserInfoResponse> accountRegisterRequest(
				RegisterRequest registerRequest
			) {
				log.error("[ERR_USER_REST_SERVICE] - FAllBackFactory Execute");
				UserInfoResponse userInfoResponse = new UserInfoResponse();
				userInfoResponse.setUuid("");
				userInfoResponse.setEmail("");
				userInfoResponse.setName("");
				userInfoResponse.setDescription("");
				userInfoResponse.setProfile("");
				userInfoResponse.setLoginLock("");
				userInfoResponse.setUserType("");
				userInfoResponse.setCreatedDate(LocalDateTime.now());
				userInfoResponse.setUpdatedDate(LocalDateTime.now());
				return new ApiResponse<>(userInfoResponse);
			}

			@Override
			public ApiResponse<UserDetailsInfoResponse> getUserDetailsInfo(
				String serviceName, String userUUID
			) {
				log.error("[ERR_USER_REST_SERVICE] - FAllBackFactory Execute");
				UserInfoResponse userInfoResponse = new UserInfoResponse();
				userInfoResponse.setUuid("");
				userInfoResponse.setEmail("");
				userInfoResponse.setName("");
				userInfoResponse.setDescription("");
				userInfoResponse.setProfile("");
				userInfoResponse.setLoginLock("");
				userInfoResponse.setUserType("");
				userInfoResponse.setCreatedDate(LocalDateTime.now());
				userInfoResponse.setUpdatedDate(LocalDateTime.now());
				UserDetailsInfoResponse userDetailsInfoResponse = new UserDetailsInfoResponse();
				userDetailsInfoResponse.setUserInfo(userInfoResponse);
				userDetailsInfoResponse.setWorkspaceInfoList(new ArrayList<>());
				return new ApiResponse<>(userDetailsInfoResponse);
			}
		};
	}
}
