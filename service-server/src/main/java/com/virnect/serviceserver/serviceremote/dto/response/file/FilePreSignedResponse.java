package com.virnect.serviceserver.serviceremote.dto.response.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class FilePreSignedResponse {
    @ApiModelProperty(value = "워크스페이스 식별자", notes = "해당 식별자를 통해 워크스페이스 구별합니다.")
    private String workspaceId;
    @ApiModelProperty(value = "원격협업 식별자", notes = "해당 식별자를 통해 파일 경로 구분 합니다..", position = 1)
    private String sessionId;
    @ApiModelProperty(value = "파일 오리지널 이름", notes = "파일 오리지널 이름 입니다", position = 2, example = "직박구리 파일")
    private String name;
    @ApiModelProperty(value = "파일 이름", notes = "파일 이름 입니다", position = 3, example = "2020-08-28_fjxqMrTwIcVmxFIjgfRC")
    private String objectName;
    @ApiModelProperty(value = "파일 타입", notes = "파일 컨텐츠 타입 입니다", position = 4, example = "image/png")
    private String contentType;
    @ApiModelProperty(value = "파일 크기", notes = "파일 크기로 byte 입니다.", dataType = "int", position = 5, example = "10")
    private long size;
    @ApiModelProperty(value = "다운로드 URL", notes = "파일 다운로드 URL 입니다.", position = 6, example = "http://url")
    private String url;
    @ApiModelProperty(value = "다운로드 URL 만료기간", notes = "다운로드 URL 만료기간 입니다.(second)", dataType = "int", position = 7, example = "3600")
    private int expiry;
}
