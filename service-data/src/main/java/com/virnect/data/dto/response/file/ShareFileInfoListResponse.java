package com.virnect.data.dto.response.file;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.data.dto.PageMetadataResponse;

@Getter
@RequiredArgsConstructor
public class ShareFileInfoListResponse {

	private final List<ShareFileInfoResponse> fileInfoList;
	private final PageMetadataResponse pageMeta;

}
