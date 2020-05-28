package com.virnect.content.dto.rest;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ContentManagement
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.29
 */

@Getter
@Setter
public class LicenseInfoResponse {
    @ApiModelProperty(value = "워크스페이스 식별자", notes = "워크 스페이스를 구별하기 위해 필요한 식별자", example = "4d6eab0860969a50acbfa4599fbb5ae8")
    private Long maxStorageSize;
    @ApiModelProperty(value = "워크스페이스 고유번호", notes = "워크스페이스에 할당된 고유 번호", position = 1, example = "532878")
    private Integer maxDownloadHit;

    @Override
    public String toString() {
        return "LicenseInfoResponse{" +
                "maxStorageSize='" + maxStorageSize + '\'' +
                ", maxDownloadHit='" + maxDownloadHit + '\'' +
                '}';
    }
}
