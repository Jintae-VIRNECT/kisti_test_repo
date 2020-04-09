package com.virnect.license.application;

import com.virnect.license.dto.request.EventCouponRequest;
import com.virnect.license.dto.request.CouponActiveRequest;
import com.virnect.license.dto.request.CouponRegisterRequest;
import com.virnect.license.dto.response.EventCouponResponse;
import com.virnect.license.dto.response.MyCouponInfoListResponse;
import com.virnect.license.global.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-License
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.09
 */
@Slf4j
@Service
public class LicenseService {
    /**
     * 이벤트 쿠폰 생성
     *
     * @param eventCouponRequest
     * @return
     */
    public ApiResponse<EventCouponResponse> generateEventCoupon(EventCouponRequest eventCouponRequest) {
        return null;
    }

    /**
     * 이벤트 쿠폰 등록
     *
     * @param couponRegisterRequest
     * @return
     */
    public ApiResponse<MyCouponInfoListResponse> couponRegister(CouponRegisterRequest couponRegisterRequest) {
        return null;
    }

    /**
     * 내 쿠폰 정보 리스트 조회
     *
     * @param userId
     * @param pageable
     * @return
     */
    public ApiResponse<MyCouponInfoListResponse> getMyCouponInfoList(String userId, Pageable pageable) {
        return null;
    }

    /**
     * 내 쿠폰 활성화
     *
     * @param couponActiveRequest
     * @return
     */
    public ApiResponse<MyCouponInfoListResponse> couponActiveHandler(CouponActiveRequest couponActiveRequest) {
        return null;
    }
}
