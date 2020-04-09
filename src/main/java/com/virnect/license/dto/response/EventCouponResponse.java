package com.virnect.license.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-License
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.09
 */
@ApiModel
@Getter
@Setter
public class EventCouponResponse {
    @ApiModelProperty(value = "쿠폰 발급 처리 결과", notes = "true 면 성공적으로 발급됨", example = "true")
    private boolean couponGenerateResult;
}
