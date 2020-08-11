package com.virnect.serviceserver.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel
public class RoomInfoResponse {
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

    /*@ApiModelProperty(value = "리더 유저 uuid", position = 4, example = "4b260e69bd6fa9a583c9bbe40f5aceb3")
    private String leaderId;

    @ApiModelProperty(value = "리더 유저 이 메일", position = 5, example = "test18@test.com")
    private String leaderEmail;*/

   /* @ApiModelProperty(value = "협업방 총 인원 수", position = 4, example = "3")
    private int totalCount;

    @ApiModelProperty(value = "협업방 접속 인원 수", position = 5, example = "2")
    private int currentCount;*/

    @ApiModelProperty(value = "협업방 접속 멤버 정보", position = 5)
    private List<MemberInfoResponse> memberList;
}
