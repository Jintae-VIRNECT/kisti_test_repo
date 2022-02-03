package com.virnect.content.domain.project;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.content.domain.BaseTimeEntity;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-09-06
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
@Entity
@Table(name = "project_download_log", indexes = {
	@Index(name = "IDX_workspace_uuid", columnList = "workspace_uuid")
})
@NoArgsConstructor
public class ProjectDownloadLog extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "project_download_log_id")
	private Long id;

	@Column(name = "workspace_uuid", nullable = false)
	private String workspaceUUID;

	@Column(name = "project_name", nullable = false)
	private String projectName;

	@Column(name = "project_size")
	private Long projectSize;

	@Column(name = "upload_user_uuid")
	private String uploadUserUUID;

	@Column(name = "download_user_UUID")
	private String downloadUserUUID;

	@Builder

	public ProjectDownloadLog(
		String workspaceUUID, String projectName, Long projectSize, String uploadUserUUID, String downloadUserUUID
	) {
		this.workspaceUUID = workspaceUUID;
		this.projectName = projectName;
		this.projectSize = projectSize;
		this.uploadUserUUID = uploadUserUUID;
		this.downloadUserUUID = downloadUserUUID;
	}
}

