package com.virnect.data.dto.rest;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.data.dto.PageMetadataResponse;

@Getter
@Setter
@NoArgsConstructor
public class WorkspaceMemberInfoListResponse {
    private List<WorkspaceMemberInfoResponse> memberInfoList;
    private PageMetadataResponse pageMeta;
}
