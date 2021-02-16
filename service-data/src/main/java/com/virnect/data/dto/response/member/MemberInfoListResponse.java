package com.virnect.data.dto.response.member;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.data.dto.response.PageMetadataResponse;

@Getter
@RequiredArgsConstructor
public class MemberInfoListResponse {
    private final List<MemberInfoResponse> memberList;
    private final PageMetadataResponse pageMeta;

}
