package com.virnect.workspace.application.user;

import com.virnect.workspace.dto.rest.*;
import com.virnect.workspace.global.common.ApiResponse;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-04-29
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Component
public class UserRestFallbackFactory implements FallbackFactory<UserRestService> {

	@Override
	public UserRestService create(Throwable cause) {
		log.error(cause.getMessage(), cause);
		return new UserRestService() {
			@Override
			public ApiResponse<UserInfoRestResponse> getUserInfoByUserId(String userId) {
				return new ApiResponse<>(new UserInfoRestResponse());
			}

			@Override
			public ApiResponse<UserInfoListRestResponse> getUserInfoListUserIdAndSearchKeyword(
				String userId, String search, boolean paging, Pageable pageable
			) {
				return new ApiResponse<>(new UserInfoListRestResponse());
			}

            @Override
            public ApiResponse<InviteUserInfoResponse> getUserInfoByEmail(String email) {
                return new ApiResponse<>(new InviteUserInfoResponse());
            }
            @Override
			public ApiResponse<UserInfoListRestResponse> getUserInfoList(String search, List<String> workspaceUserIdList) {
				UserInfoListRestResponse userInfoListRestResponse = new UserInfoListRestResponse();
				userInfoListRestResponse.setUserInfoList(UserInfoListRestResponse.EMPTY);
				userInfoListRestResponse.setPageMeta(null);
				return new ApiResponse<>(userInfoListRestResponse);
			}

            @Override
			public ApiResponse<UserInfoRestResponse> registerMemberRequest(
				RegisterMemberRequest registerMemberRequest, String serviceID
			) {
				return new ApiResponse<>(new UserInfoRestResponse());
			}

			@Override
			public ApiResponse<UserDeleteRestResponse> userDeleteRequest(
				String userUUId, String serviceID
			) {
				return new ApiResponse<>(new UserDeleteRestResponse());
			}

			@Override
			public ApiResponse<UserInfoAccessCheckResponse> userInfoAccessCheckRequest(
				String userId, UserInfoAccessCheckRequest userInfoAccessCheckRequest
			) {
				return new ApiResponse<>(new UserInfoAccessCheckResponse());
			}

			@Override
			public ApiResponse<MemberUserPasswordChangeResponse> memberUserPasswordChangeRequest(
				String serviceID,
				MemberUserPasswordChangeRequest memberUserPasswordChangeRequest
			) {
				return new ApiResponse<>(
					new MemberUserPasswordChangeResponse(false, "", "", LocalDateTime.now())
				);
			}
		};
	}
}
