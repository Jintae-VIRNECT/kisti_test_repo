package com.virnect.service.dto.file.response;

import com.virnect.service.dto.PageMetadataResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class FileInfoListResponse {
    private final List<FileInfoResponse> fileInfoList;
    private final PageMetadataResponse pageMeta;
}
