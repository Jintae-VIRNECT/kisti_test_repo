package com.virnect.license.global.error;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Project: base
 * DATE: 2020-01-07
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@ApiModel
public enum ErrorCode {
    // 쿠폰 생성
    ERR_CREATE_COUPON(1000, "Coupon Generate fail."),
    ERR_ALREADY_REGISTER_EVENT_COUPON(1001, "Already event coupon is registered"),

    // 쿠폰 등록
    ERR_COUPON_NOT_FOUND(2000, "Coupon not found by serial key"),
    ERR_COUPON_REGISTER_ALREADY_USED(2001, "Coupon is already use"),
    ERR_COUPON_REGISTER_EXPIRED(2002, "Coupon is expired"),

    // 쿠폰 사용
    ERR_COUPON_ACTIVE_NOT_FOUND(3000, "Coupon activated fail. coupon information not found"),

    // 공통 에러
    ERR_API_AUTHENTICATION(8003, "Authentication Error"),
    ERR_INVALID_VALUE(8004, "Invalid Value"),
    ERR_INVALID_REQUEST_PARAMETER(8001, "Invalid request parameter cause api errors"),
    ERR_UNEXPECTED_SERVER_ERROR(9999, "Unexpected Server Error, Please contact Administrator");

    @ApiModelProperty(name = "code")
    private int code;
    @ApiModelProperty(name = "message")
    private String message;

    ErrorCode(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
