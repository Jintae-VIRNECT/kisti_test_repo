package com.virnect.api.dto.response;

import com.virnect.api.dto.PageMetadataResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class RoomHistoryInfoListResponse {
    private final List<RoomHistoryInfoResponse> roomHistoryInfoList;
    private final PageMetadataResponse pageMeta;
}
