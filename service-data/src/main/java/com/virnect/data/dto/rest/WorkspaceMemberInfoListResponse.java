package com.virnect.data.dto.rest;

import com.virnect.data.dto.PageMetadataResponse;
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
