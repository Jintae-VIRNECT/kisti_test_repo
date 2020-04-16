package com.virnect.license.api;

import com.virnect.license.application.LicenseService;
import com.virnect.license.dto.request.CouponActiveRequest;
import com.virnect.license.dto.request.CouponRegisterRequest;
import com.virnect.license.dto.request.EventCouponRequest;
import com.virnect.license.dto.response.EventCouponResponse;
import com.virnect.license.dto.response.MyCouponInfoListResponse;
import com.virnect.license.dto.response.MyCouponInfoResponse;
import com.virnect.license.exception.LicenseServiceException;
import com.virnect.license.global.common.ApiResponse;
import com.virnect.license.global.common.PageRequest;
import com.virnect.license.global.error.ErrorCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-License
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.09
 */

@Api
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/licenses")
public class LicenseController {
    private final LicenseService licenseService;

    @ApiOperation(value = "이벤트 쿠폰 발급 요청")
    @PostMapping("/event/coupon")
    public ResponseEntity<ApiResponse<EventCouponResponse>> requestEventCouponRequestHandler(@RequestBody @Valid EventCouponRequest eventCouponRequest, BindingResult result) {
        if (result.hasErrors()) {
            result.getAllErrors().forEach(System.out::println);
            throw new LicenseServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<EventCouponResponse> responseMessage = this.licenseService.generateEventCoupon(eventCouponRequest);
        return ResponseEntity.ok(responseMessage);
    }

    @ApiOperation(value = "쿠폰 등록")
    @PostMapping("/coupon/register")
    public ResponseEntity<ApiResponse<MyCouponInfoResponse>> couponRegisterRequestHandler(@RequestBody @Valid CouponRegisterRequest couponRegisterRequest, BindingResult result){
        if(result.hasErrors()){
            result.getAllErrors().forEach(System.out::println);
            throw new LicenseServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<MyCouponInfoResponse> responseMessage = this.licenseService.couponRegister(couponRegisterRequest);
        return ResponseEntity.ok(responseMessage);
    }

    @ApiOperation(value = "내 쿠폰 정보 리스트 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "검색어(이메일/사용자명)", dataType = "string", paramType = "path", defaultValue = "498b1839dc29ed7bb2ee90ad6985c608"),
            @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "2"),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(0부터 시작)", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "createdDate,desc"),
    })
    @GetMapping("/coupon/{userId}")
    public ResponseEntity<ApiResponse<MyCouponInfoListResponse>> getMyCouponInfoListRequestHandler(@PathVariable("userId") String userId, @ApiIgnore PageRequest pageable){
        if(StringUtils.isEmpty(userId)){
            log.info("[userId] is invalid");
            throw new LicenseServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<MyCouponInfoListResponse> responseMessage = this.licenseService.getMyCouponInfoList(userId, pageable.of());
        return ResponseEntity.ok(responseMessage);
    }

    @ApiOperation(value = "내 쿠폰 사용")
    @PutMapping("/coupon/active")
    public ResponseEntity<ApiResponse<MyCouponInfoResponse>> couponActiveRequestHandler(@RequestBody @Valid CouponActiveRequest couponActiveRequest, BindingResult result){
        if(result.hasErrors()){
            result.getAllErrors().forEach(System.out::println);
            throw new LicenseServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<MyCouponInfoResponse> responseMessage = this.licenseService.couponActiveHandler(couponActiveRequest);
        return ResponseEntity.ok(responseMessage);
    }

}

