package com.virnect.api.dto.response;

import com.virnect.serviceserver.gateway.dto.PageMetadataResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class MemberInfoListResponse {
    private final List<MemberInfoResponse> memberList;
    private final PageMetadataResponse pageMeta;

}
