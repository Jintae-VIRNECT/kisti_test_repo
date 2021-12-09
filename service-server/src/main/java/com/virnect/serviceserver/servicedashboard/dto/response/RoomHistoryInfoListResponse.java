package com.virnect.serviceserver.servicedashboard.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.data.dto.PageMetadataResponse;

@Getter
@Setter
@NoArgsConstructor
public class RoomHistoryInfoListResponse {

	private List<RoomHistoryInfoResponse> roomHistoryInfoList;
	private PageMetadataResponse pageMeta;

	@Builder
	public RoomHistoryInfoListResponse(
		List<RoomHistoryInfoResponse> roomHistoryInfoList,
		PageMetadataResponse pageMeta
	) {
		this.pageMeta = pageMeta;
		this.roomHistoryInfoList = roomHistoryInfoList;
	}

}
