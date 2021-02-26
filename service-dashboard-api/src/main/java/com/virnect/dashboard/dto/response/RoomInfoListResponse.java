package com.virnect.dashboard.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.dashboard.dto.PageMetadataResponse;

@Getter
@RequiredArgsConstructor
public class RoomInfoListResponse {
    private final List<RoomInfoResponse> roomInfoList;
    private final PageMetadataResponse pageMeta;
}
