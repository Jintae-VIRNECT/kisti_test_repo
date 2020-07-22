package com.virnect.serviceserver.gateway.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ApiModel
public class RoomHistoryDetailInfoResponse {
    @ApiModelProperty(value = "원격협업 방 식별자", example = "ses_NxKh1OiT2S")
    private String sessionId;

    @ApiModelProperty(value = "협업 방 이름", position = 1, example = "Remote")
    private String title;

    @ApiModelProperty(value = "협업 방 소개", position = 2, example = "This is Remote Collaborate, or.. Conference Room(Session)!!")
    private String description;

    @ApiModelProperty(value = "협업 방 프로필 주소", position = 3, example = "default")
    private String profile;

   /* @ApiModelProperty(value = "협업방 총 인원 수", position = 4, example = "3")
    private String totalCount;

    @ApiModelProperty(value = "협업방 접속 인원 수", position = 5, example = "2")
    private String currentCount;*/

    @ApiModelProperty(value = "협업방 생성 일자", position = 4, example = "2020-01-20T14:05:30")
    private LocalDateTime createdDate;

    @ApiModelProperty(value = "최종 협업방 정보 수정 일자", position = 5, example = "2020-01-20T14:05:30")
    private LocalDateTime updatedDate;

    @ApiModelProperty(value = "협업방 시작 일자", position = 6, example = "2020-01-20T14:05:30")
    private LocalDateTime activeDate;

    @ApiModelProperty(value = "협업방 종료 일자", position = 7, example = "2020-01-20T14:05:30")
    private LocalDateTime unactiveDate;

    @ApiModelProperty(value = "협업 진행 시간", position = 8, example = "3600")
    private Long durationSec;

    @ApiModelProperty(value = "협업방 접속 멤버 정보", position = 9)
    private List<MemberInfoResponse> memberList;
}
