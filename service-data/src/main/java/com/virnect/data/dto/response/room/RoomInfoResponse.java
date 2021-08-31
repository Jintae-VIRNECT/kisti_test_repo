package com.virnect.data.dto.response.room;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import com.virnect.data.domain.session.SessionType;
import com.virnect.data.dto.response.member.MemberInfoResponse;

@Getter
@Setter
@ApiModel
@Builder
public class RoomInfoResponse {
    @ApiModelProperty(value = "Remote Session Identifier", example = "ses_NxKh1OiT2S")
    private String sessionId;

    @ApiModelProperty(value = "Remote Session Title", position = 1, example = "Remote")
    private String title;

    @ApiModelProperty(value = "Remote Session Description", position = 2, example = "This is Remote Collaborate, or.. Conference Room(Session)!!")
    private String description;

    @ApiModelProperty(value = "Remote Session Profile Image URL", position = 3, example = "default")
    private String profile;

    @ApiModelProperty(value = "Remote Session Maximum User Capacity", position = 4, example = "3")
    private int maxUserCount;

    @ApiModelProperty(value = "Remote Session Type", position = 5, example = "PRIVATE")
    private SessionType sessionType;

    @ApiModelProperty(value = "Remote Session Allocated Member Information List", position = 6)
    private List<MemberInfoResponse> memberList;
}
