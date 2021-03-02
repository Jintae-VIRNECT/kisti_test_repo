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
public class RoomHistoryInfoResponse {

	@ApiModelProperty(value = "Remote Session No", position = 1, example = "1")
	private int no;

	@ApiModelProperty(value = "Remote Session Identifier", position = 2, example = "ses_NxKh1OiT2S")
	private String sessionId;

	@ApiModelProperty(value = "Remote Session Title", position = 3, example = "Remote")
	private String title;

	@ApiModelProperty(value = "Remote Session Description", position = 4, example = "This is Remote Collaborate, or.. Conference Room(Session)!!")
	private String description;

	@ApiModelProperty(value = "Remote Session Profile Image URL", position = 5, example = "default")
	private String profile;

	@ApiModelProperty(value = "Remote Session Leader Nick Name", position = 6, example = "")
	private String leaderNickName;

	@ApiModelProperty(value = "Remote Session Maximum User Capacity", position = 7, example = "3")
	private int maxUserCount;

	@ApiModelProperty(value = "Remote Session Type", position = 8, example = "PRIVATE")
	private SessionType sessionType;

	@ApiModelProperty(value = "Remote Session Activation Date", position = 9, example = "2020-01-20T14:05:30")
	private LocalDateTime activeDate;

	@ApiModelProperty(value = "Remote Session UnActivation Date", position = 10, example = "2020-01-20T14:05:30")
	private LocalDateTime unactiveDate;

	@ApiModelProperty(value = "Remote Session Duration time", position = 11, example = "3600")
	private Long durationSec;

	@ApiModelProperty(value = "Remote Session status", position = 12)
	private boolean status;

	@ApiModelProperty(value = "Remote Session server record count", position = 13)
	private Long serverRecord;

	@ApiModelProperty(value = "Remote Session local record count", position = 14)
	private Long localRecord;

	@ApiModelProperty(value = "Remote Session attach file count", position = 15)
	private Long attach;

	@ApiModelProperty(value = "Remote Session Allocated Member Information List", position = 16)
	private List<MemberInfoResponse> memberList;

	@Override
	public String toString() {
		return "RoomHistoryInfoResponse{" +
			"no=" + no +
			", sessionId='" + sessionId + '\'' +
			", title='" + title + '\'' +
			", description='" + description + '\'' +
			", profile='" + profile + '\'' +
			", leaderNickName='" + leaderNickName + '\'' +
			", maxUserCount=" + maxUserCount +
			", sessionType=" + sessionType +
			", activeDate=" + activeDate +
			", unactiveDate=" + unactiveDate +
			", durationSec=" + durationSec +
			", status=" + status +
			", serverRecord=" + serverRecord +
			", localRecord=" + localRecord +
			", attach=" + attach +
			", memberList=" + memberList +
			'}';
	}
}
