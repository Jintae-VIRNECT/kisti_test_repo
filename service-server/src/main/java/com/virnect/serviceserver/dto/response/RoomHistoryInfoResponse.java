package com.virnect.serviceserver.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ApiModel
public class RoomHistoryInfoResponse {
    @ApiModelProperty(value = "원격협업 방 식별자", example = "ses_NxKh1OiT2S")
    private String sessionId;

    @ApiModelProperty(value = "협업 방 이름", position = 1, example = "Remote")
    private String title;

    @ApiModelProperty(value = "협업 방 소개", position = 2, example = "This is Remote Collaborate, or.. Conference Room(Session)!!")
    private String description;

    @ApiModelProperty(value = "협업 방 프로필 주소", position = 3, example = "default")
    private String profile;

    @ApiModelProperty(value = "협업방 최대 참여인원 수", position = 4, example = "3")
    private int maxUserCount;

    @ApiModelProperty(value = "협업방 시작 일자", position = 5, example = "2020-01-20T14:05:30")
    private LocalDateTime activeDate;

    @ApiModelProperty(value = "협업방 종료 일자", position = 6, example = "2020-01-20T14:05:30")
    private LocalDateTime unactiveDate;

    @ApiModelProperty(value = "협업 진행 시간", position = 7, example = "3600")
    private Long durationSec;

    @ApiModelProperty(value = "협업방 접속 멤버 정보", position = 8)
    private List<MemberInfoResponse> memberList;
}
