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
	private List<RecordServerFileInfoResponse> infos;

	public RecordServerFileInfoListResponse(
		int currentPage, int totalPage,
		List<RecordServerFileInfoResponse> infos
	) {
		this.currentPage = currentPage;
		this.totalPage = totalPage;
		this.infos = infos;
	}

	@Override
	public String toString() {
		return "RecordServerFileInfoListResponse{" +
			"currentPage=" + currentPage +
			", totalPage=" + totalPage +
			", infos=" + infos +
			'}';
	}
}


