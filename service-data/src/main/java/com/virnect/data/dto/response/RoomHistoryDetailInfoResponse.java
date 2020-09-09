package com.virnect.data.dto.response;

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
    @ApiModelProperty(value = "Remote Session Identifier", example = "ses_NxKh1OiT2S")
    private String sessionId;

    @ApiModelProperty(value = "Remote Session Title", position = 1, example = "Remote")
    private String title;

    @ApiModelProperty(value = "Remote Session Description", position = 2, example = "This is Remote Collaborate, or.. Conference Room(Session)!!")
    private String description;

    @ApiModelProperty(value = "Remote Session Profile Image URL", position = 3, example = "default")
    private String profile;

    @ApiModelProperty(value = "Remote Session Creation Date", position = 4, example = "2020-01-20T14:05:30")
    private LocalDateTime createdDate;

    @ApiModelProperty(value = "Remote Session Update Date", position = 5, example = "2020-01-20T14:05:30")
    private LocalDateTime updatedDate;

    @ApiModelProperty(value = "Remote Session Activation Date", position = 6, example = "2020-01-20T14:05:30")
    private LocalDateTime activeDate;

    @ApiModelProperty(value = "Remote Session UnActivation Date", position = 7, example = "2020-01-20T14:05:30")
    private LocalDateTime unactiveDate;

    @ApiModelProperty(value = "Remote Session Duration time", position = 8, example = "3600")
    private Long durationSec;

    @ApiModelProperty(value = "Remote Session Allocated Member Information List", position = 9)
    private List<MemberInfoResponse> memberList;
}
