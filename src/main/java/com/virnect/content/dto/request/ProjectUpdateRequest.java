package com.virnect.content.dto.request;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.util.CollectionUtils;

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
public class ProjectUpdateRequest {
	@ApiModelProperty(value = "업데이트 유저 식별자", example = "498b1839dc29ed7bb2ee90ad6985c608", position = 1, required = true)
	@NotBlank
	private String userUUID;
	@ApiModelProperty(value = "프로젝트 이름", example = "", position = 2)
	private String name;
	@ApiModelProperty(value = "프로젝트 파일", example = "", position = 3)
	private String project;
	@ApiModelProperty(value = "프로젝트 속성", example = "", position = 4)
	private PropertyInfoDTO properties;
	@ApiModelProperty(value = "타겟 정보", position = 5)
	private ProjectTargetRequest target;
	@ApiModelProperty(value = "모드 정보", example = "", position = 6)
	private List<Mode> modeList;
	@ApiModelProperty(value = "공유 정보", position = 7)
	private SharePermissionRequest share;
	@ApiModelProperty(value = "모드 정보", position = 8)
	private EditPermissionRequest edit;

	@Override
	public String toString() {
		return "ProjectUpdateRequest{" +
			"userUUID='" + userUUID + '\'' +
			", name='" + name + '\'' +
			", project='" + project + '\'' +
			", properties=" + properties +
			", target=" + target +
			", modeList=" + modeList +
			", share=" + share +
			", edit=" + edit +
			'}';
	}

	@ApiModelProperty(hidden = true)
	public boolean isTargetWidthUpdate() {
		return target != null && target.getWidth() > 0;
	}

	@ApiModelProperty(hidden = true)
	public boolean isTargetLengthUpdate() {
		return target != null && target.getLength() > 0;
	}
}
