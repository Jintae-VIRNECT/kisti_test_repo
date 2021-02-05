package com.virnect.serviceserver.dto.response.member;

import com.virnect.serviceserver.dto.response.PageMetadataResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class MemberInfoListResponse {
    private final List<MemberInfoResponse> memberList;
    private final PageMetadataResponse pageMeta;

}
