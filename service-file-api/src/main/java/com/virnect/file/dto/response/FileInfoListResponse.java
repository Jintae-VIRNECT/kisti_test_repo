package com.virnect.file.dto.response;

import com.virnect.file.dto.PageMetadataResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class FileInfoListResponse {
    private final List<FileInfoResponse> fileInfoList;
    private final PageMetadataResponse pageMeta;
}
