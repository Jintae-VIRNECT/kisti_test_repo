package com.virnect.data.dto.rest;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RecordServerFileInfoListResponse {

	private int currentPage;
	private int totalPage;
	private List<RecordServerFileInfoResponse> recordServerFileInfoResponses;

	public RecordServerFileInfoListResponse(
		int currentPage, int totalPage,
		List<RecordServerFileInfoResponse> recordServerFileInfoResponses
	) {
		this.currentPage = currentPage;
		this.totalPage = totalPage;
		this.recordServerFileInfoResponses = recordServerFileInfoResponses;
	}

	@Override
	public String toString() {
		return "RecordServerFileInfoListResponse{" +
			"currentPage=" + currentPage +
			", totalPage=" + totalPage +
			", infos=" + recordServerFileInfoResponses +
			'}';
	}
}


