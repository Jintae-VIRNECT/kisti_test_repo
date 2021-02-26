package com.virnect.data.application.user;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.dto.rest.UserInfoListOnlyResponse;
import com.virnect.data.dto.rest.UserInfoListResponse;
import com.virnect.data.dto.rest.UserInfoResponse;
import com.virnect.data.global.common.ApiResponse;

@Slf4j
@Component
public class UserRestFallbackFactory implements FallbackFactory<UserRestService> {

    @Override
    public UserRestService create(Throwable cause) {
        log.info(cause.getMessage(), cause);
        return new UserRestService() {
            @Override
            public ApiResponse<UserInfoListResponse> getUserInfoList(String search, boolean paging) {
                log.info("[USER WORKSPACE LIST API FALLBACK] => USER_ID: {}");
                UserInfoListResponse empty = new UserInfoListResponse();
                empty.setUserInfoList(new ArrayList<>());
                return new ApiResponse<UserInfoListResponse>(empty);
            }

            @Override
            public ApiResponse<UserInfoListResponse> getUserInfoList(boolean paging) {
                return null;
            }

            @Override
            public ApiResponse<UserInfoResponse> getUserInfoByUserId(String userId) {
                log.info("[USER INFORMATION API FALLBACK] => USER_ID: {}", userId);
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
        };

        /*return (search, paging) -> {
            log.info("[USER WORKSPACE LIST API FALLBACK] => USER_ID: {}");
            UserInfoListResponse empty = new UserInfoListResponse();
            empty.setUserInfoList(new ArrayList<>());
            return new ApiResponse<UserInfoListResponse>(empty);
        };*/
    }
}
