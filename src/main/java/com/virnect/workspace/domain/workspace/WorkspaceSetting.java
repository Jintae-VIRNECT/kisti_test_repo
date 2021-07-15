package com.virnect.workspace.domain.workspace;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.virnect.workspace.domain.TimeEntity;
import org.hibernate.envers.Audited;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Project: PF-Workspace
 * DATE: 2020-10-16
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Audited
@Table(name = "workspace_setting")
public class WorkspaceSetting extends TimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "workspace_setting_id")
	private Long id;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "default_logo", nullable = false)
	private String defaultLogo;

	@Column(name = "grey_logo", nullable = true)
	private String greyLogo;

	@Column(name = "white_logo", nullable = true)
	private String whiteLogo;

	@Column(name = "favicon", nullable = false)
	private String favicon;

	@Builder
	WorkspaceSetting(String title, String defaultLogo, String greyLogo, String whiteLogo, String favicon) {
		this.title = title;
		this.defaultLogo = defaultLogo;
		this.greyLogo = greyLogo;
		this.whiteLogo = whiteLogo;
		this.favicon = favicon;
	}
}
