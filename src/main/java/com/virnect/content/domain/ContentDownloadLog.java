package com.virnect.content.domain;

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

@Getter
@Setter
@Entity
@Table(name = "content_download_log", indexes = {
	@Index(name = "IDX_workspace_uuid", columnList = "workspace_uuid,created_at")
})
@NoArgsConstructor
public class ContentDownloadLog extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "content_download_log_id")
	private Long id;

	@Column(name = "workspace_uuid", nullable = false)
	private String workspaceUUID;

	@Column(name = "content_name", nullable = false)
	private String contentName;

	@Column(name = "content_size")
	private Long contentSize;

	@Column(name = "upload_user_uuid")
	private String uploadUserUUID;

	@Column(name = "download_user_UUID")
	private String downloadUserUUID;

	@Builder
	public ContentDownloadLog(
		String workspaceUUID, String contentName, long contentSize, String contentUploader, String downloader
	) {
		this.workspaceUUID = workspaceUUID;
		this.contentName = contentName;
		this.contentSize = contentSize;
		this.uploadUserUUID = contentUploader;
		this.downloadUserUUID = downloader;
	}
}
