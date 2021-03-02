package com.virnect.data.dto.rest;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LicenseInfoResponse {
    @ApiModelProperty(value = "라이선스 식별자", example = "1")
    private int id;
    @ApiModelProperty(value = "라이선스 타입", position = 1, example = "BASIC")
    private String licenseType;
    @ApiModelProperty(value = "라이선스 할당 제품", position = 2, example = "REMOTE")
    private String productName;
    @ApiModelProperty(value = "라이선스 시리얼 키", position = 3, example = "29923633-47D7-439C-B515-94FF4A9B5BB0")
    private String serialKey;
    @ApiModelProperty(value = "라이선스 상태", position = 4, example = "USE")
    private String status;
    @ApiModelProperty(value = "라이선스 정보 생성일", position = 5, example = "2020-04-16T16:34:35")
    private LocalDateTime createdDate;
    @ApiModelProperty(value = "라이선스 정보 수정일", position = 6, example = "2020-04-16T16:34:35")
    private LocalDateTime updatedDate;

    @Override
    public String toString() {
        return "LicenseInfoResponse{" +
                "id='" + id + '\'' +
                ", licenseType='" + licenseType + '\'' +
                ", productName='" + productName + '\'' +
                ", serialKey='" + serialKey + '\'' +
                ", status='" + status + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", updatedDate=" + updatedDate +
                '}';
    }
}
