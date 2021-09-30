package com.virnect.serviceserver.servicedashboard.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.data.dto.PageMetadataResponse;

@Getter
@RequiredArgsConstructor
public class RoomInfoListResponse {
    private final List<RoomInfoResponse> roomInfoList;
    private final PageMetadataResponse pageMeta;
}
