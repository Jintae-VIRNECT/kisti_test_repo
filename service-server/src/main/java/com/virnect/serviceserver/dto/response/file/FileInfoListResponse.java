package com.virnect.serviceserver.dto.response.file;

import com.virnect.serviceserver.dto.response.PageMetadataResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class FileInfoListResponse {
    private final List<FileInfoResponse> fileInfoList;
    private final PageMetadataResponse pageMeta;
}
