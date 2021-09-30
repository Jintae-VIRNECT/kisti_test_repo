package com.virnect.data.application.account;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.dto.rest.GuestAccountInfoResponse;
import com.virnect.data.dto.rest.UserInfoListOnlyResponse;
import com.virnect.data.dto.rest.UserInfoListResponse;
import com.virnect.data.dto.rest.UserInfoResponse;
import com.virnect.data.global.common.ApiResponse;

@Slf4j
@Component
public class AccountRestFallbackFactory implements FallbackFactory<AccountRestService> {

    @Override
    public AccountRestService create(Throwable cause) {
        log.info(cause.getMessage(), cause);
        return new AccountRestService() {
            @Override
            public ApiResponse<UserInfoListResponse> getUserInfoList(String search, boolean paging) {
                log.info("[ACCOUNT WORKSPACE LIST API FALLBACK] => USER_ID: {}");
                UserInfoListResponse empty = new UserInfoListResponse();
                empty.setUserInfoList(new ArrayList<>());
                return new ApiResponse<>(empty);
            }

            @Override
            public ApiResponse<UserInfoListResponse> getUserInfoList(boolean paging) {
                return null;
            }

            @Override
            public ApiResponse<UserInfoResponse> getUserInfoByUserId(String userId) {
                log.info("[ACCOUNT INFORMATION API FALLBACK] => USER_ID: {}", userId);
                UserInfoResponse empty = new UserInfoResponse();
                return new ApiResponse<>(empty);
            }

            @Override
            public ApiResponse<UserInfoListResponse> getUserInfo(boolean paging) {
                return null;
            }

            @Override
            public ApiResponse<UserInfoListOnlyResponse> getUserInfoListByUserUUIDArray(String[] uuid) {
                return null;
            }

            @Override
            public ApiResponse<GuestAccountInfoResponse> getGuestAccountInfo(
                String product, String workspaceId, String xGuestUserAgent, String xGuestUserIp
            ) {
                log.info("[GUEST ACCOUNT INFORMATION API FALLBACK] => WORKSPACE ID : {}", workspaceId);
                GuestAccountInfoResponse empty = new GuestAccountInfoResponse();
                return new ApiResponse<>(empty);
            }
        };

    }
}
