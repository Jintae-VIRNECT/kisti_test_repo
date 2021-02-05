package com.virnect.serviceserver.dto.response.room;

import com.virnect.serviceserver.dto.response.PageMetadataResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class RoomInfoListResponse {
    private final List<RoomInfoResponse> roomInfoList;
    private final PageMetadataResponse pageMeta;
}
