package com.virnect.content.dto.request;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

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
public class ProjectUpdateRequest {
	@ApiModelProperty(value = "업데이트 유저 식별자", example = "498b1839dc29ed7bb2ee90ad6985c608", position = 1, required = true)
	@NotBlank
	private String userUUID;
	@ApiModelProperty(value = "프로젝트 이름", example = "", position = 2)
	private String name;
	@ApiModelProperty(value = "프로젝트 파일", example = "", position = 3)
	private MultipartFile project;
	@ApiModelProperty(value = "프로젝트 속성", example = "", position = 4)
	@PropertyValidated(message = "프로젝트 속성정보가 유효하지 않습니다.")
	private String properties;
	@ApiModelProperty(value = "타겟 타입", example = "", position = 5)
	private TargetType targetType;
	@ApiModelProperty(value = "타겟 데이터", example = "", position = 6)
	private String targetData;
	@ApiModelProperty(value = "타겟 가로 사이즈", example = "", position = 7)
	private long targetWidth = 0L;
	@ApiModelProperty(value = "타겟 세로 사이즈", example = "", position = 8)
	private long targetLength = 0L;
	@ApiModelProperty(value = "타겟 파일", example = "", position = 9)
	private MultipartFile targetFile;
	@ApiModelProperty(value = "모드 정보", example = "", position = 10)
	private List<Mode> modeList;
	@ApiModelProperty(value = "공유 정보", example = "", position = 11)
	private SharePermission sharePermission;
	@ApiModelProperty(value = "공유 권한 대상 특정 유저 목록", example = "", position = 12)
	private List<String> sharedUserList;
	@ApiModelProperty(value = "편집 정보", example = "", position = 13)
	private EditPermission editPermission;
	@ApiModelProperty(value = "편집 권한 대상 특정 유저 목록", example = "", position = 14)
	private List<String> editUserList;

	public boolean validateTargetType() {
		if (targetType == TargetType.QR && !StringUtils.hasText(targetData)) {
			return false;
		}
		if (targetType == TargetType.Image && targetFile == null) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ProjectUpdateRequest{" +
			"userUUID='" + userUUID + '\'' +
			", name='" + name + '\'' +
			", project=" + project +
			//", properties='" + properties + '\'' +
			", targetType=" + targetType +
			", targetData='" + targetData + '\'' +
			", targetWidth=" + targetWidth +
			", targetLength=" + targetLength +
			", targetFile=" + targetFile +
			", modeList=" + modeList +
			", sharePermission=" + sharePermission +
			", sharedUserList=" + sharedUserList +
			", editPermission=" + editPermission +
			", editUserList=" + editUserList +
			'}';
	}
}
