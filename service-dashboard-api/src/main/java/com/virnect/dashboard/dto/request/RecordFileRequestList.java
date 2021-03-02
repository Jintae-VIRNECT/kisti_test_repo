package com.virnect.dashboard.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecordFileRequestList {

	private String createdAt;
	private String filename;
	private String id;
	private Integer limit;
	private String order;
	private Integer page;
	private String sessionId;

	@Override
	public String toString() {
		return "OptionGetRecordFileListRequest{" +
			"createdAt='" + createdAt + '\'' +
			", filename='" + filename + '\'' +
			", id='" + id + '\'' +
			", limit=" + limit +
			", order='" + order + '\'' +
			", page=" + page +
			", sessionId='" + sessionId + '\'' +
			'}';
	}
}
