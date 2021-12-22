package com.virnect.process.dto.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jiyong.heo
 * @project PF-ProcessManagement
 * @email jiyong.heo@virnect.com
 * @description
 * @since 2020.07.02
 */
@Getter
@Setter
public class TroubleMemoUploadRequest {
	@ApiModelProperty(value = "트러블 메모 사진")
	private String photoFile;

	@ApiModelProperty(value = "트러블 메모 사진 설명", position = 1)
	private String caption;

	@ApiModelProperty(value = "트러블 메모 작성자 식별자", position = 2)
	@NotBlank
	private String workerUUID;

	@ApiModelProperty(value = "트러블 메모 작성자의 워크스페이스 식별자", position = 3)
	@NotBlank
	private String workspaceUUID;

	@Override
	public String toString() {
		return "TroubleMemoUploadRequest{" +
			"caption='" + caption + '\'' +
			", workerUUID='" + workerUUID + '\'' +
			", workspaceUUID='" + workspaceUUID + '\'' +
			'}';
	}
}
