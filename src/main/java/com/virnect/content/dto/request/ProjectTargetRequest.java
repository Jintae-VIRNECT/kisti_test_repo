package com.virnect.content.dto.request;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.content.domain.TargetType;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-09-02
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
@ApiModel
public class ProjectTargetRequest {
	@ApiModelProperty(value = "타입", example = "QR", position = 0, required = true)
	@NotNull
	private TargetType type;
	@ApiModelProperty(value = "QR 타겟 데이터", notes = "컨텐츠의 식별자 이므로 프로젝트 내에서는 타겟 데이터가 중복될 수 있음.", example = "0f518d23-9226-4c8d-a488-c6581ef90456", position = 1, required = false)
	private String data;
	@ApiModelProperty(value = "타겟 너비 사이즈", example = "10", position = 2, required = true)
	@NotNull
	private long width;
	@ApiModelProperty(value = "타겟 길이 사이즈", example = "10", position = 3, required = true)
	@NotNull
	private long length;
	@ApiModelProperty(value = "이미지 타겟 파일 업로드 URL", example = "https://192.168.6.3:2838/virnect-platform/workspace/report/2021-08-31_fg8nvnjzwv.png", position = 4, required = false)
	private String file;

	@Override
	public String toString() {
		return "ProjectTargetRequest{" +
			"type=" + type +
			", data='" + data + '\'' +
			", width=" + width +
			", length=" + length +
			", file='" + file + '\'' +
			'}';
	}
}
