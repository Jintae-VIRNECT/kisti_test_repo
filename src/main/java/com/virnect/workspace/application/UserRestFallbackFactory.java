package com.virnect.workspace.application;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import com.virnect.workspace.dto.rest.InviteUserInfoRestResponse;
import com.virnect.workspace.dto.rest.RegisterMemberRequest;
import com.virnect.workspace.dto.rest.UserDeleteRestResponse;
import com.virnect.workspace.dto.rest.UserInfoAccessCheckRequest;
import com.virnect.workspace.dto.rest.UserInfoAccessCheckResponse;
import com.virnect.workspace.dto.rest.UserInfoListRestResponse;
import com.virnect.workspace.dto.rest.UserInfoRestResponse;
import com.virnect.workspace.global.common.ApiResponse;

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
            public ApiResponse<UserInfoListRestResponse> getUserInfoListUserIdAndSearchKeyword(String userId, String search, boolean paging, Pageable pageable) {
                return new ApiResponse<>(new UserInfoListRestResponse());
            }

            @Override
            public ApiResponse<InviteUserInfoRestResponse> getUserInfoByEmailList(String[] emailList) {
                return new ApiResponse<>(new InviteUserInfoRestResponse());
            }

            @Override
            public ApiResponse<UserInfoListRestResponse> getUserInfoList(String search, String[] workspaceUserIdList) {
                UserInfoListRestResponse userInfoListRestResponse = new UserInfoListRestResponse();
                userInfoListRestResponse.setUserInfoList(UserInfoListRestResponse.EMPTY);
                userInfoListRestResponse.setPageMeta(null);
                return new ApiResponse<>(userInfoListRestResponse);
            }

            @Override
            public ApiResponse<UserInfoRestResponse> registerMemberRequest(
                RegisterMemberRequest registerMemberRequest
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
        };
    }
}
