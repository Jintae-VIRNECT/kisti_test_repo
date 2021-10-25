package com.virnect.content.dto.request;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.content.global.common.FileResourceType;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-09-02
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class FileResourceUploadRequest {
	@ApiModelProperty(value = "업로드 대상 파일 리소스", example = "", position = 0, required = true)
	@NotNull
	private MultipartFile file;
	@ApiModelProperty(value = "파일 업로드 타입", example = "CONTENT", position = 1, required = true)
	@NotNull
	private FileResourceType type;

	@Override
	public String toString() {
		return "FileResourceUploadRequest{" +
			"file=" + file +
			", type=" + type +
			'}';
	}
}

