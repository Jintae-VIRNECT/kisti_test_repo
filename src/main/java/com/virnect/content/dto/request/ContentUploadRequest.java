package com.virnect.content.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.virnect.content.domain.TargetType;
import com.virnect.content.domain.Types;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-01-22
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Content Upload Request DTO Class
 */

@Getter
@Setter
public class ContentUploadRequest {

	private String targetData;

	private TargetType targetType;

	@NotBlank
	private String workspaceUUID;

	@NotNull
	private MultipartFile content;

	private Types contentType;

	@NotBlank
	private String name;

	//private String metadata;

	@NotBlank
	private String properties;

	@NotBlank
	private String userUUID;

	@Builder
	public ContentUploadRequest(
		String targetData, TargetType targetType, @NotBlank String workspaceUUID, @NotNull MultipartFile content,
		Types contentType, @NotBlank String name,@NotBlank String properties,
		@NotBlank String userUUID
	) {
		this.targetData = targetData;
		this.targetType = targetType;
		this.workspaceUUID = workspaceUUID;
		this.content = content;
		this.contentType = contentType;
		this.name = name;
		//this.metadata = metadata;
		this.properties = properties;
		this.userUUID = userUUID;
	}

	@Override
	public String toString() {
		return "ContentUploadRequest{" +
			"targetData='" + targetData + '\'' +
			", targetType=" + targetType +
			", workspaceUUID='" + workspaceUUID + '\'' +
			", content=" + content +
			", contentType=" + contentType +
			", name='" + name + '\'' +
			//", properties='" + properties + '\'' +
			", userUUID='" + userUUID + '\'' +
			'}';
	}
}
