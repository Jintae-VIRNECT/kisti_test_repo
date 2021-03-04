package com.virnect.serviceserver.servicedashboard.dto.response;

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
public class RoomDetailInfoResponse {
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

    @ApiModelProperty(value = "Remote Session Type", position = 6, example = "PRIVATE")
    private SessionType sessionType;

    @ApiModelProperty(value = "Remote Session Activation Date", position = 7, example = "2020-01-20T14:05:30")
    private LocalDateTime activeDate;

    @ApiModelProperty(value = "Remote Session Allocated Member Information List", position = 8)
    private List<MemberInfoResponse> memberList;
}
