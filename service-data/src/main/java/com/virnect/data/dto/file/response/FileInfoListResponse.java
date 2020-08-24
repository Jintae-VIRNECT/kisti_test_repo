package com.virnect.data.dto.file.response;

import com.virnect.data.dto.PageMetadataResponse;
import com.virnect.data.dto.response.MemberInfoResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class FileInfoListResponse {
    private final List<FileInfoResponse> fileInfoList;
    private final PageMetadataResponse pageMeta;
}
