package com.virnect.serviceserver.dto.rest;

import com.virnect.serviceserver.dto.response.PageMetadataResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class WorkspaceMemberInfoListResponse {
    private List<WorkspaceMemberInfoResponse> memberInfoList;
    private PageMetadataResponse pageMeta;
}
