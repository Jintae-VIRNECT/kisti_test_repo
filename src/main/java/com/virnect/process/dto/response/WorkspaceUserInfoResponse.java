package com.virnect.process.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class WorkspaceUserInfoResponse {
    @ApiModelProperty(value = "작업자 식별자", notes = "작업자의 식별자", example = "449ae69cee53b8a6819053828c94e496")
    private String workerUUID;
    @ApiModelProperty(value = "사용자 이름", position = 1, example = "VIRNECT Master")
    private String workerName;
    @ApiModelProperty(value = "사용자 프로필 이미지 URL", position = 2, example = "VIRNECT 워크스페이스 유저")
    private String workerProfile;
    @ApiModelProperty(value = "사용자 이메일", position = 3, example = "ljk@virnect.com")
    private String workerEmail;
    @ApiModelProperty(value = "작업 진행중인 세부 작업 수", notes = "작업자에게 할당된 세부 작업들 중 작업이 진행중인 세부 작업의 개수", position = 4, example = "1")
    private Integer countProgressing;
    @ApiModelProperty(value = "할당된 세부 작업 수", notes = "작업자에게 할당된 세부 작업의 총 개수", position = 5, example = "5")
    private Integer countAssigned;
    @ApiModelProperty(value = "진행률")
    private Integer percent;
    @ApiModelProperty(value = "ㅇ")
    private Long countContent;
    @ApiModelProperty(value = "")
    private LocalDateTime lastestReportedTime;
}
