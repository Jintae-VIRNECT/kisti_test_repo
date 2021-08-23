package com.virnect.content.dto.request;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.content.domain.EditPermission;
import com.virnect.content.domain.Mode;
import com.virnect.content.domain.SharePermission;
import com.virnect.content.domain.TargetType;
import com.virnect.content.global.common.PropertyValidated;

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
	@ApiModelProperty(value = "프로젝트 파일", example = "", position = 3, required = true, dataType = "__file")
	private MultipartFile project;
	@ApiModelProperty(value = "프로젝트 속성", example = "{\"name\":\"string\",\"sceneGroupList\":[{\"sceneGroupName\":\"string\",\"sceneList\":[{\"sceneName\":\"string\",\"objectList\":[{\"objectName\":\"string\"}]}]}]}", position = 4, required = true)
	@PropertyValidated
	private String properties;
	@ApiModelProperty(value = "타겟 타입", example = "QR", position = 5, required = true)
	@NotNull
	private TargetType targetType;
	/*@ApiModelProperty(value = "타겟 데이터", example = "QR", position = 6, required = true)
	@NotBlank
	private String targetData;*/
	@ApiModelProperty(value = "타겟 사이즈", example = "10", position = 7, required = false)
	@NotNull
	private Long targetSize;
	@ApiModelProperty(value = "타겟 파일", example = "", position = 8, required = false)
	private MultipartFile targetFile;
	@ApiModelProperty(value = "모드 정보", example = "[\"TWO_DIMENSINAL\", \"THREE_DIMENSINAL\", \"TWO_OR_THREE_DIMENSINAL\"]", position = 9, required = true)
	@NotNull
	private List<Mode> modeList;
	@ApiModelProperty(value = "공유 정보", example = "SPECIFIC_MEMBER", position = 10, required = true)
	@NotNull
	private SharePermission sharePermission;
	@ApiModelProperty(value = "공유 권한 대상 특정 유저 목록", example = "[\"4ea61b4ad1dab12fb2ce8a14b02b7460\"]", position = 11, required = false)
	private List<String> sharedUserList;
	@ApiModelProperty(value = "편집 정보", example = "SPECIFIC_MEMBER", position = 12, required = true)
	@NotNull
	private EditPermission editPermission;
	@ApiModelProperty(value = "편집 권한 대상 특정 유저 목록", example = "[\"4ea61b4ad1dab12fb2ce8a14b02b7460\"]", position = 13, required = false)
	private List<String> editUserList;

}
