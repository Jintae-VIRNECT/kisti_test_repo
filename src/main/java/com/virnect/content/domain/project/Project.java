package com.virnect.content.domain.project;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.content.domain.BaseTimeEntity;
import com.virnect.content.domain.EditPermission;
import com.virnect.content.domain.SharePermission;
import com.virnect.content.domain.TargetType;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-08-17
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Entity
@Getter
@Setter
@Audited
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "project", indexes = {
	@Index(name = "idx_uuid", columnList = "uuid"),
	@Index(name = "idx_name", columnList = "name"),
	@Index(name = "idx_user_uuid", columnList = "user_uuid"),
	@Index(name = "idx_workspace_uuid", columnList = "workspace_uuid")
})
public class Project extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "project_id")
	private Long id;

	@Column(name = "uuid", nullable = false, unique = true)
	private String uuid;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "path", nullable = false)
	private String path;

	@Column(name = "size", nullable = false)
	private Long size; //byte

	@Column(name = "share_permission", nullable = false)
	@Enumerated(EnumType.STRING)
	private SharePermission sharePermission;

	@Column(name = "edit_permission", nullable = false)
	@Enumerated(EnumType.STRING)
	private EditPermission editPermission;

	@Column(name = "user_uuid", nullable = false)
	private String userUUID;

	@Column(name = "workspace_uuid", nullable = false)
	private String workspaceUUID;

	@Lob
	@Column(name = "properties", nullable = false)
	private String properties;

	@NotAudited
	@OneToMany(mappedBy = "project")
	List<ProjectMode> projectModeList = new ArrayList<>();

	@NotAudited
	@OneToMany(mappedBy = "project")
	List<ProjectShareUser> projectShareUserList = new ArrayList<>();

	@NotAudited
	@OneToMany(mappedBy = "project")
	List<ProjectEditUser> projectEditUserList = new ArrayList<>();

	@NotAudited
	@OneToMany(mappedBy = "project")
	List<ProjectActivityLog> projectActivityLogList = new ArrayList<>();

	@NotAudited
	@OneToOne(mappedBy = "project")
	ProjectTarget projectTarget;

	@Builder
	public Project(
		String uuid, String name, String path, Long size, SharePermission sharePermission,
		EditPermission editPermission,
		String userUUID,
		String workspaceUUID,
		String properties
	) {
		this.uuid = uuid;
		this.name = name;
		this.path = path;
		this.size = size;
		this.sharePermission = sharePermission;
		this.editPermission = editPermission;
		this.userUUID = userUUID;
		this.workspaceUUID = workspaceUUID;
		this.properties = properties;
	}

	public boolean isFileTypeTarget() {
		return projectTarget.getType() == TargetType.Image || projectTarget.getType() == TargetType.QR;
	}
}
