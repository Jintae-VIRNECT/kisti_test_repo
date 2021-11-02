package com.virnect.content.domain.project;

import javax.persistence.Column;
import javax.persistence.Entity;
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
	private String activity;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "user_uuid", nullable = false)
	private String userUUID;

	@Column(name = "user_nickname", nullable = false)
	private String userNickName;

	@Column(name = "user_profile_image", nullable = false)
	private String userProfileImage;

	@ManyToOne(targetEntity = Project.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	private Project project;

	@Builder
	public ProjectActivityLog(
		String activity, Long userId, String userUUID, String userNickName, String userProfileImage,
		Project project
	) {
		this.activity = activity;
		this.userId = userId;
		this.userUUID = userUUID;
		this.userNickName = userNickName;
		this.userProfileImage = userProfileImage;
		this.project = project;
	}
}
