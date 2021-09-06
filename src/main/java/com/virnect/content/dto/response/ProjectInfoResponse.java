package com.virnect.content.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.content.domain.EditPermission;
import com.virnect.content.domain.Mode;
import com.virnect.content.domain.SharePermission;
import com.virnect.content.dto.request.PropertyInfoDTO;
import com.virnect.content.global.common.PropertyValidated;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-08-19
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class ProjectInfoResponse {
	@ApiModelProperty(value = "프로젝트 식별자", position = 0, example = "")
	private String uuid = "";
	@ApiModelProperty(value = "프로젝트 이름", position = 1, example = "")
	private String name = "";
	@ApiModelProperty(value = "프로젝트 파일 경로", position = 2, example = "")
	private String path = "";
	@ApiModelProperty(value = "프로젝트 파일 크기", position = 3, example = "0")
	private long size = 0L;
	@ApiModelProperty(value = "프로젝트 구성 정보", position = 4, example = "")
	private PropertyInfoDTO property;
	@ApiModelProperty(value = "프로젝트 업로더 식별자", position = 5, example = "")
	private String uploaderUUID = "";
	@ApiModelProperty(value = "프로젝트 업로더 유저 닉네임", position = 6, example = "")
	private String uploaderName = "";
	@ApiModelProperty(value = "프로젝트 업로더 유저 프로필 이미지", position = 7, example = "")
	private String uploaderProfile = "";
	@ApiModelProperty(value = "프로젝트 2D 모드 활성화 여부", position = 8, example = "")
	private boolean mode2D;
	@ApiModelProperty(value = "프로젝트 3D 모드 활성화 여부", position = 8, example = "")
	private boolean mode3D;
	@ApiModelProperty(value = "프로젝트 2D +3D 모드 활성화 여부", position = 8, example = "")
	private boolean mode2D3D;
	@ApiModelProperty(value = "프로젝트 편집 권한", position = 9, example = "")
	private EditPermission editPermission;
	@ApiModelProperty(value = "프로젝트 편집 권한 멤버 목록", position = 10, example = "")
	private List<String> editUserList = new ArrayList<>();
	@ApiModelProperty(value = "프로젝트 공유 권한", position = 11, example = "")
	private SharePermission sharePermission;
	@ApiModelProperty(value = "프로젝트 공유 권한 멤버 목록", position = 12, example = "")
	private List<String> sharedUserList = new ArrayList<>();
	@ApiModelProperty(value = "씬 그룹 수", position = 13, example = "")
	private int propertySceneGroupTotal = 0;
	@ApiModelProperty(value = "씬 수", position = 14, example = "")
	private int propertySceneTotal = 0;
	@ApiModelProperty(value = "오브젝트 수", position = 15, example = "")
	private int propertyObjectTotal = 0;
	@ApiModelProperty(value = "프로젝트 타겟 정보", position = 16, example = "")
	private ProjectTargetInfoResponse targetInfo;
	@ApiModelProperty(value = "프로젝트 생성 일자", position = 17, example = "")
	private LocalDateTime createdDate = LocalDateTime.now();
	@ApiModelProperty(value = "프로젝트 수정 일자", position = 18, example = "")
	private LocalDateTime updatedDate = LocalDateTime.now();
}
