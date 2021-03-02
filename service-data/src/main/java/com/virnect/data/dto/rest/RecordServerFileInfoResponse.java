package com.virnect.data.dto.rest;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RecordServerFileInfoResponse {

	@ApiModelProperty(value = "Record file creatAt")
	private String createAt;

	@ApiModelProperty(value = "Record file duration")
	private int duration;

	@ApiModelProperty(value = "Record file filename")
	private String filename;

	@ApiModelProperty(value = "Record file framerate")
	private int framerate;

	/*@ApiModelProperty(value = "Record file metaData")
	private String[] metaData;*/

	@ApiModelProperty(value = "Record file recordingId")
	private String recordingId;

	@ApiModelProperty(value = "Record file resolution")
	private String resolution;

	@ApiModelProperty(value = "Record file sessionId")
	private String sessionId;

	@ApiModelProperty(value = "Record file size")
	private int size;

	@ApiModelProperty(value = "Record file userId")
	private String userId;

	@ApiModelProperty(value = "Record file workspaceId")
	private String workspaceId;

}
