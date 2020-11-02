package com.virnect.data.dto.file.response;

import com.virnect.data.dto.PageMetadataResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class FileDetailInfoListResponse {
    private final List<FileDetailInfoResponse> fileDetailInfoList;
    private final PageMetadataResponse pageMeta;
}
