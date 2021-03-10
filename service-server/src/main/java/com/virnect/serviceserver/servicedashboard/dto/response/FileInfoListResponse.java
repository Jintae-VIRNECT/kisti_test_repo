package com.virnect.serviceserver.servicedashboard.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class FileInfoListResponse {

	private List<FileInfoResponse> fileInfoList;

	public FileInfoListResponse(List<FileInfoResponse> fileInfoList) {
		this.fileInfoList = fileInfoList;
	}
}
