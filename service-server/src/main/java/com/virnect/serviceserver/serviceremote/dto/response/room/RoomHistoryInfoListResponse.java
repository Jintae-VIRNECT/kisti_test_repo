package com.virnect.serviceserver.serviceremote.dto.response.room;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.data.dto.PageMetadataResponse;

@Getter
@RequiredArgsConstructor
public class RoomHistoryInfoListResponse {
    private final List<RoomHistoryInfoResponse> roomHistoryInfoList;
    private final PageMetadataResponse pageMeta;
}
