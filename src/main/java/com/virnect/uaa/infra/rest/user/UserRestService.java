package com.virnect.uaa.infra.rest.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.virnect.uaa.domain.auth.dto.user.request.RegisterRequest;
import com.virnect.uaa.global.common.ApiResponse;
import com.virnect.uaa.infra.rest.user.dto.UserDetailsInfoResponse;
import com.virnect.uaa.infra.rest.user.dto.UserInfoResponse;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description User service server rest client interface
 * @since 2020.03.20
 */
@FeignClient(name = "user-server")
public interface UserRestService {
	@PostMapping(value = "/users/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	ApiResponse<UserInfoResponse> accountRegisterRequest(@ModelAttribute RegisterRequest registerRequest);

	@GetMapping("/users/info")
	ApiResponse<UserDetailsInfoResponse> getUserDetailsInfo(
		@RequestHeader("service") String serviceName,
		@RequestHeader("x-auth-uuid") String userUUID
	);
}
