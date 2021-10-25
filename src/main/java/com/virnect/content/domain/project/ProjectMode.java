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

import org.hibernate.envers.Audited;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.content.domain.BaseTimeEntity;
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
@Entity
@Table(name = "project_mode")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectMode extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "project_mode_id")
	private Long id;

	@Column(name = "mode", nullable = false)
	@Enumerated(EnumType.STRING)
	private Mode mode;

	@ManyToOne(targetEntity = Project.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	private Project project;

	@Builder
	public ProjectMode(Mode mode, Project project) {
		this.mode = mode;
		this.project = project;
	}
}
