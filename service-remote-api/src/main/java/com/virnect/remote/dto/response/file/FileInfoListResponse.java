package com.virnect.remote.dto.response.file;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.data.dto.PageMetadataResponse;

@Getter
@RequiredArgsConstructor
public class FileInfoListResponse {
    private final List<FileInfoResponse> fileInfoList;
    private final PageMetadataResponse pageMeta;
}
