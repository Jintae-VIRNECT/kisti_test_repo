package com.virnect.data.dto.rest;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ListRecordingFilesResponse {

	private int currentPage;
	private List<RecordingFiles> infos;

	public ListRecordingFilesResponse(
		int currentPage,
		List<RecordingFiles> infos
	) {
		this.currentPage = currentPage;
		this.infos = infos;
	}

	@Override
	public String toString() {
		return "RecordServerFileInfoListResponse{" +
			"currentPage=" + currentPage +
			", infos=" + infos +
			'}';
	}
}


