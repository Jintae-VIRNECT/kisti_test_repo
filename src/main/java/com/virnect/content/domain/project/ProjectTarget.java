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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.domain.BaseTimeEntity;
import com.virnect.content.domain.TargetType;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-08-18
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Getter
@Setter
@Entity
@Table(name = "project_target")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectTarget extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "project_target_id")
	private Long id;

	@Column(name = "type", nullable = false)
	@Enumerated(EnumType.STRING)
	private TargetType type;

	@Column(name = "path", nullable = true)
	private String path;

	@Column(name = "width", nullable = false)
	private Long width;

	@Column(name = "length", nullable = false)
	private Long length;

	@OneToOne(targetEntity = Project.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	private Project project;

	@Builder
	public ProjectTarget(
		TargetType type, String path, Long width, Long length, Project project
	) {
		this.type = type;
		this.path = path;
		this.width = width;
		this.length = length;
		this.project = project;
	}
}
