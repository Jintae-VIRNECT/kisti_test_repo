package com.virnect.content.dto.request;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.content.domain.Mode;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-08-18
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
@ApiModel
public class ProjectUploadRequest {
	@ApiModelProperty(value = "워크스페이스 식별자", example = "4d6eab0860969a50acbfa4599fbb5ae8", position = 0, required = true)
	@NotBlank
	private String workspaceUUID;
	@ApiModelProperty(value = "업로드 유저 식별자", example = "498b1839dc29ed7bb2ee90ad6985c608", position = 1, required = true)
	@NotBlank
	private String userUUID;
	@ApiModelProperty(value = "프로젝트 이름", example = "프로젝트", position = 2, required = true)
	@NotBlank
	private String name;
	@ApiModelProperty(value = "프로젝트 파일", example = "https://192.168.6.3:2838/virnect-platform/workspace/project/36658ce4-9570-40e0-9be6-653a3bbc6373.Ares", position = 3, required = true)
	@NotBlank
	private String project;
	@ApiModelProperty(value = "프로젝트 속성", example = "{\"propertyName\":\"프로젝트 이름\",\"propertyObjectList\":[{\"objectName\":\"1-depth 첫번째 씬그룹\",\"objectType\":\"SceneGroup\",\"objectChildList\":[{\"objectName\":\"2-depth 씬\",\"objectType\":\"Scene\",\"objectChildList\":[{\"objectName\":\"3-depth 오브젝트111\",\"objectType\":\"Text\"}]}]}]}", position = 4, required = true)
	@NotNull
	private PropertyInfoDTO properties;
	@ApiModelProperty(value = "타겟 정보", position = 5, required = true)
	@NotNull
	private ProjectTargetRequest target;
	@ApiModelProperty(value = "모드 정보", example = "[\"TWO_DIMENSINAL\", \"THREE_DIMENSINAL\", \"TWO_OR_THREE_DIMENSINAL\"]", position = 6, required = true)
	@NotNull
	private List<Mode> modeList;
	@ApiModelProperty(value = "공유 정보", position = 7, required = true)
	@NotNull
	private SharePermissionRequest share;
	@ApiModelProperty(value = "모드 정보", position = 8, required = true)
	@NotNull
	private EditPermissionRequest edit;

	@Override
	public String toString() {
		return "ProjectUploadRequest{" +
			"workspaceUUID='" + workspaceUUID + '\'' +
			", userUUID='" + userUUID + '\'' +
			", name='" + name + '\'' +
			", project='" + project + '\'' +
			", properties=" + properties +
			", target=" + target +
			", modeList=" + modeList +
			", share=" + share +
			", edit=" + edit +
			'}';
	}
}
