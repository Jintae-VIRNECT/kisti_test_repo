package com.virnect.data.dto.file.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@ApiModel
public class FileInfoResponse {
    @ApiModelProperty(value = "워크스페이스 식별자", notes = "해당 식별자를 통해 워크스페이스 구별합니다.")
    private String workspaceId;
    @ApiModelProperty(value = "원격협업 식별자", notes = "해당 식별자를 통해 파일 경로 구분 합니다..", position = 1)
    private String sessionId;
    @ApiModelProperty(value = "파일 이름", notes = "파일 이름 입니다", position = 2, example = "직박구리 파일")
    private String name;
    @ApiModelProperty(value = "파일 크기", notes = "파일 크기로 byte 입니다.", dataType = "int", position = 5, example = "10")
    private long size;
    @ApiModelProperty(value = "저장 경로", notes = "파일이 저장된 경로(url)", position = 9, example = "http://localhost:8000/workspaceId/sessionId/file.extention")
    private String path;
    @ApiModelProperty(value = "파일 생성 일자", notes = "생성일자(신규 등록) 기간 정보입니다.", position = 12, example = "2020-02-15 16:32:13")
    private LocalDateTime createdDate; //upload date?
}
