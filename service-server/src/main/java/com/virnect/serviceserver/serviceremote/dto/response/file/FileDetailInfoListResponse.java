package com.virnect.serviceserver.serviceremote.dto.response.file;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.data.dto.PageMetadataResponse;

@Getter
@RequiredArgsConstructor
public class FileDetailInfoListResponse {
    private final List<FileDetailInfoResponse> fileDetailInfoList;
    private final PageMetadataResponse pageMeta;
}
