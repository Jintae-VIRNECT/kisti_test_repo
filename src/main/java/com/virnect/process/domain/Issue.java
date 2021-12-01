package com.virnect.process.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
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

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-02-04
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Getter
@Setter
@Entity
@Audited
@Table(name = "issue")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Issue extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "issue_id")
	private Long id;

	@Column(name = "content")
	private String content;

	@Column(name = "photo_file_path")
	private String path;

	@Column(name = "worker_uuid")
	private String workerUUID;

	@Column(name = "workspace_uuid")
	private String workspaceUUID;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "job_id")
	private Job job;

	@Builder
	public Issue(String content, String path, String workerUUID, String workspaceUUID, Job job) {
		this.content = content;
		this.path = path;
		this.workerUUID = workerUUID;
		this.workspaceUUID = workspaceUUID;
		this.job = job;
	}

	@Override
	public String toString() {
		return "Issue{" +
			"id=" + id +
			", content='" + content + '\'' +
			", path='" + path + '\'' +
			", workerUUID='" + workerUUID + '\'' +
			", workspaceUUID='" + workspaceUUID + '\'' +
			'}';
	}
}
