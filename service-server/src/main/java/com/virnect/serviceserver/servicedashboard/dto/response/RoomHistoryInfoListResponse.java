package com.virnect.serviceserver.servicedashboard.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.data.dto.PageMetadataResponse;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class RoomHistoryInfoListResponse {

	private PageMetadataResponse pageMeta;
	private List<RoomHistoryInfoResponse> roomHistoryInfoList;

	public RoomHistoryInfoListResponse(
		PageMetadataResponse pageMeta,
		List<RoomHistoryInfoResponse> roomHistoryInfoList
	) {
		this.pageMeta = pageMeta;
		this.roomHistoryInfoList = roomHistoryInfoList;
	}

	public RoomHistoryInfoListResponse(List<RoomHistoryInfoResponse> roomHistoryInfoList) {
		this.roomHistoryInfoList = roomHistoryInfoList;
	}
}
