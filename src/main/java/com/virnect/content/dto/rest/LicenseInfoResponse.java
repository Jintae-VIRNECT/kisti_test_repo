package com.virnect.content.dto.rest;

import io.swagger.annotations.ApiModel;
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
    @ApiModelProperty(value = "워크스페이스 최대 저장 용량(MB 단위)", notes = "라이선스의 최대 용량 (MB단위)", position = 1, example = "800")
    private Long maxStorageSize;
    @ApiModelProperty(value = "워크스페이스 최대 컨텐츠 다운로드 수", notes = "라이선스의 컨텐츠 최대 다운로드 수", position = 2, example = "5000")
    private Long maxDownloadHit;
    @ApiModelProperty(value = "워크스페이스 총 사용 용량(byte 단위)", notes ="워크스페이스 기준 byte 단위의 총 사용 용량", position = 3)
    private Long workspaceStorage;
    @ApiModelProperty(value = "업로드 용량(byte 단위)", notes ="컨텐츠 파일의 용량 (byte 단위)", position = 4)
    private Long uploadSize;
    @ApiModelProperty(value = "워크스페이스 총 다운로드 수", notes = "워크스페이스 기준 총 다운로드 수", position =5)
    private Long workspaceDownloadHit;
    @ApiModelProperty(value = "남은 용량", notes = "라이선스의 남은 용량", position = 6)
    private Long usableCapacity;
    @ApiModelProperty(value = "남은 다운로드 수", notes = "라이선스의 남은 다운로드 수", position = 7)
    private Long usableDownloadHit;

    @Override
    public String toString() {
        return "LicenseInfoResponse{" +
                "maxStorageSize='" + maxStorageSize + '\'' +
                ", workspaceStorage='" + workspaceStorage + '\'' +
                ", uploadSize='" + uploadSize + '\'' +
                ", maxDownloadHit='" + maxDownloadHit + '\'' +
                ", workspaceDownloadHit='" + workspaceDownloadHit + '\'' +
                ", usableCapacity='" + usableCapacity + "\'" +
                ", usableDownloadHit=" + usableDownloadHit + "\'" +
                '}';
    }
}
