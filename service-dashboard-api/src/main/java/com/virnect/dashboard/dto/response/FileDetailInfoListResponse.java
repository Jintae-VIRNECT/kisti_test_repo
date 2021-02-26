package com.virnect.dashboard.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class FileDetailInfoListResponse {

	private List<FileDetailInfoResponse> fileDetailInfoList;

	public FileDetailInfoListResponse(List<FileDetailInfoResponse> fileDetailInfoList) {
		this.fileDetailInfoList = fileDetailInfoList;
	}
}
