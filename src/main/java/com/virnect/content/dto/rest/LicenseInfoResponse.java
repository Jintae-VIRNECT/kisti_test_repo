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
    @ApiModelProperty(value = "워크스페이스 최대 저장 용량(byte 단위)", notes = "byte 단위의 최대 용량", position = 1, example = "9663676416")
    private Long maxStorageSize;
    @ApiModelProperty(value = "워크스페이스 최대 컨텐츠 다운로드 수", position = 2, example = "5000")
    private Integer maxDownloadHit;
    @ApiModelProperty(value = "워크스페이스 총 용량(byte 단위)", notes ="byte 단위의 총 용량", position = 3)
    private Long workspaceStorage;
    @ApiModelProperty(value = "업로드 용량", notes ="byte 단위의 총 용량", position = 4)
    private Long uploadSize;
    @ApiModelProperty(value = "워크스페이스 총 다운로드 수", position =5)
    private Long workspaceDownloadHit;

    @Override
    public String toString() {
        return "LicenseInfoResponse{" +
                "maxStorageSize='" + maxStorageSize + '\'' +
                ", workspaceStorage='" + workspaceStorage + '\'' +
                ", uploadSize='" + uploadSize + '\'' +
                ", maxDownloadHit='" + maxDownloadHit + '\'' +
                ", workspaceDownloadHit='" + workspaceDownloadHit + '\'' +
                '}';
    }
}
