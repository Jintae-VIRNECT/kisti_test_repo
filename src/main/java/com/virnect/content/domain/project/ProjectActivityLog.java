package com.virnect.content.domain.project;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.content.domain.BaseTimeEntity;
import com.virnect.content.dto.rest.WorkspaceUserResponse;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-11-02
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "project_activity_log")
public class ProjectActivityLog extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "activity", nullable = false)
	@Enumerated(EnumType.STRING)
	private ProjectActivity activity;

	@Column(name = "activity_property")
	private String activityProperty;

	@Column(name = "user_uuid", nullable = false)
	private String userUUID;

	@Column(name = "user_name", nullable = false)
	private String userName;

	@Column(name = "user_nickname", nullable = false)
	private String userNickname;

	@Column(name = "user_profile_image", nullable = false)
	private String userProfileImage;

	@ManyToOne(targetEntity = Project.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	private Project project;

	@Builder(builderMethodName = "projectActivityLogBuilder")
	public ProjectActivityLog(
		ProjectActivity activity,
		String activityProperty,
		Project project,
		WorkspaceUserResponse workspaceUserInfo
	) {
		this.activity = activity;
		this.activityProperty = activityProperty;
		this.userUUID = workspaceUserInfo.getUuid();
		this.userName = workspaceUserInfo.getName();
		this.userNickname = workspaceUserInfo.getNickName();
		this.userProfileImage = workspaceUserInfo.getProfile();
		this.project = project;
	}
}
