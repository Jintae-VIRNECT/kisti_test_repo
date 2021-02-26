package com.virnect.dashboard.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.data.domain.session.SessionType;

@Getter
@Setter
@ApiModel
public class RoomInfoResponse {
    @ApiModelProperty(value = "Remote Session Identifier", example = "ses_NxKh1OiT2S")
    private String sessionId;

    @ApiModelProperty(value = "Remote Session Title", position = 1, example = "Remote")
    private String title;

    @ApiModelProperty(value = "Remote Session Description", position = 2, example = "This is Remote Collaborate, or.. Conference Room(Session)!!")
    private String description;

    @ApiModelProperty(value = "Remote Session Activation Date", position = 3, example = "2020-01-20T14:05:30")
    private LocalDateTime activeDate;

    @ApiModelProperty(value = "Remote Session Profile Image URL", position = 4, example = "default")
    private String profile;

    @ApiModelProperty(value = "Remote Session Maximum User Capacity", position = 5, example = "3")
    private int maxUserCount;

    @ApiModelProperty(value = "Remote Session Room Status", position = 6, example = "0")
    private boolean status;

    @ApiModelProperty(value = "Remote Session Type", position = 7, example = "PRIVATE")
    private SessionType sessionType;

    @ApiModelProperty(value = "Remote Session Allocated Member Information List", position = 8)
    private List<MemberInfoResponse> memberList;
}
