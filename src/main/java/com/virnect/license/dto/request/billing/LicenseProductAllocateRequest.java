package com.virnect.license.dto.request.billing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ApiModel
public class LicenseProductAllocateRequest {
    @NotNull
    @Positive
    @ApiModelProperty(value = "사용자 대표 식별자", example = "1")
    private long userId;
    @ApiModelProperty(value = "정기 결제 요청 여부(최초 요청 건 or 정기 결제 요청 건)", position = 1, example = "false")
    @NotNull
    @AssertTrue
    @AssertFalse
    private boolean regularRequest = false;
    @NotBlank
    @ApiModelProperty(value = "결제 식별자", position = 2, example = "202019239941")
    private String paymentId;
    @NotNull
    @ApiModelProperty(value = "결제 일자", position = 3, example = "2020-06-03T18:00:11")
    private LocalDateTime paymentDate;
    @NotBlank
    @ApiModelProperty(value = "결제 국가 코드", position = 4, example = "KO")
    private String userCountryCode;
    @NotBlank
    @ApiModelProperty(value = "지급 인증 코드", position = 5, example = "48254844-235e-4421-b713-4ea682994a98")
    private String assignAuthCode;
    @ApiModelProperty(value = "결제 상품 정보", position = 6)
    @NotNull
    @Size(min = 1)
    private List<LicenseAllocateProductInfoResponse> productList;
    @ApiModelProperty(value = "결제에 사용된 쿠폰 정보", position = 7)
    private List<LicenseAllocateCouponInfoResponse> couponList;

    @Override
    public String toString() {
        return "LicenseProductAllocateRequest{" +
                "userId=" + userId +
                ", regularRequest=" + regularRequest +
                ", paymentId='" + paymentId + '\'' +
                ", paymentDate=" + paymentDate +
                ", userCountryCode='" + userCountryCode + '\'' +
                ", assignAuthCode='" + assignAuthCode + '\'' +
                ", productList=" + productList +
                ", couponList=" + couponList +
                '}';
    }
}
