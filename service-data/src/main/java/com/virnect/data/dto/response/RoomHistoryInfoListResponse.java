package com.virnect.data.dto.response;

import com.virnect.data.dto.PageMetadataResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class RoomHistoryInfoListResponse {
    private final List<RoomHistoryInfoResponse> roomHistoryInfoList;
    private final PageMetadataResponse pageMeta;
}
