package com.virnect.serviceserver.serviceremote.dto.response.room;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.data.domain.session.SessionType;
import com.virnect.serviceserver.serviceremote.dto.response.member.MemberInfoResponse;

@Getter
@Setter
@ApiModel
public class RoomHistoryDetailInfoResponse {
    @ApiModelProperty(value = "Remote Session Identifier", example = "ses_NxKh1OiT2S")
    private String sessionId = "";

    @ApiModelProperty(value = "Remote Session Title", position = 1, example = "Remote")
    private String title = "";

    @ApiModelProperty(value = "Remote Session Description", position = 2, example = "This is Remote Collaborate, or.. Conference Room(Session)!!")
    private String description = "";

    @ApiModelProperty(value = "Remote Session Profile Image URL", position = 3, example = "default")
    private String profile = "";

    @ApiModelProperty(value = "Remote Session Type", position = 4, example = "PRIVATE")
    private SessionType sessionType = SessionType.PRIVATE;

    @Deprecated
    @ApiModelProperty(value = "Remote Session Creation Date", position = 5, example = "2020-01-20T14:05:30")
    private LocalDateTime createdDate;

    @Deprecated
    @ApiModelProperty(value = "Remote Session Update Date", position = 6, example = "2020-01-20T14:05:30")
    private LocalDateTime updatedDate;

    @ApiModelProperty(value = "Remote Session Activation Date", position = 7, example = "2020-01-20T14:05:30")
    private LocalDateTime activeDate = LocalDateTime.now();

    @ApiModelProperty(value = "Remote Session UnActivation Date", position = 8, example = "2020-01-20T14:05:30")
    private LocalDateTime unactiveDate = LocalDateTime.now();

    @ApiModelProperty(value = "Remote Session Duration time", position = 9, example = "3600")
    private Long durationSec = 0L;

    @ApiModelProperty(value = "Remote Session Allocated Member Information List", position = 10)
    private List<MemberInfoResponse> memberList = new ArrayList<>();
}
