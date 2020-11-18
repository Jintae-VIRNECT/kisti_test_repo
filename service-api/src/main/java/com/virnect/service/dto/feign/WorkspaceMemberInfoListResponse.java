package com.virnect.service.dto.feign;

import com.virnect.service.dto.PageMetadataResponse;
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
