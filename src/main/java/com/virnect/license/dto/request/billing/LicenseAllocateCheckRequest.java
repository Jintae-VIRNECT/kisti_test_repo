package com.virnect.license.dto.request.billing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.List;

@Getter
@Setter
@ApiModel
public class LicenseAllocateCheckRequest {
    @ApiModelProperty(value = "사용자 대표 식별자")
    @NotNull
    @Positive
    private long userId;
    @ApiModelProperty(value = "정기 결제 요청 여부(최초 요청 건 or 정기 결제 요청 건)", position = 1, example = "false")
    @NotNull
    @AssertTrue
    @AssertFalse
    private boolean isRegularRequest = false;
    @ApiModelProperty(value = "상품 정보 리스트", position = 2)
    @NotNull
    @Size(min = 1)
    private List<LicenseAllocateProductInfoResponse> productList;
    @ApiModelProperty(value = "쿠폰 정보 리스트", position = 3)
    private List<LicenseAllocateCouponInfoResponse> couponList;

    @Override
    public String toString() {
        return "LicenseAllocateCheckRequest{" +
                "userId=" + userId +
                ", isRegularRequest=" + isRegularRequest +
                ", productList=" + productList +
                ", couponList=" + couponList +
                '}';
    }
}
