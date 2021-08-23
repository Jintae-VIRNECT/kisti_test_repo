package com.virnect.content.dto.request;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-08-20
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
@ToString
@ApiModel
public class PropertyInfoRequest {
	@ApiModelProperty(value = "프로젝트/컨텐츠 이름", example = "프로젝트네이밍", position = 0, required = true)
	private String name;
	@ApiModelProperty(value = "씬 그룹 목록", example = "", position = 1, required = true)
	private List<SceneGroup> sceneGroupList;

	@Getter
	@Setter
	@ApiModel
	public static class SceneGroup {
		@ApiModelProperty(value = "씬 그룹 이름", example = "씬그룹네이밍", position = 0, required = true)
		private String sceneGroupName;
		@ApiModelProperty(value = "씬 목록", example = "", position = 1, required = true)
		private List<Scene> sceneList;
	}

	@Getter
	@Setter
	@ApiModel
	public static class Scene {
		@ApiModelProperty(value = "씬 그룹 이름", example = "씬네임", position = 0, required = true)
		private String sceneName;
		@ApiModelProperty(value = "오브젝트 목록", example = "", position = 0, required = true)
		private List<SceneObject> objectList;
	}

	@Getter
	@Setter
	@ApiModel
	public static class SceneObject {
		@ApiModelProperty(value = "오브젝트 이름", example = "오브젝트네임", position = 0, required = true)
		private String objectName;
	}

}
