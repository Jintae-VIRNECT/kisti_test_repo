package com.virnect.download.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Project: PF-Download
 * DATE: 2020-04-20
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Entity
@Getter
@Setter
@Table(
	name = "app",
	indexes = {
		@Index(name = "uidx_uuid", columnList = "uuid", unique = true),
		@Index(name = "idx_app_version_code", columnList = "version_code"),
		@Index(name = "idx_os_id", columnList = "os_id"),
		@Index(name = "idx_device_id", columnList = "device_id"),
		@Index(name = "idx_product_id", columnList = "product_id")
	}
)
@NoArgsConstructor
public class App extends TimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "app_id")
	private Long id;

	@Column(name = "uuid", nullable = false, unique = true)
	private String uuid;

	@Column(name = "app_url", nullable = false)
	private String appUrl;

	@Column(name = "guide_url")
	private String guideUrl;

	@Column(name = "image_url", nullable = false)
	private String imageUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_id", nullable = false)
	private Device device;

	@JoinColumn(name = "product_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Product product;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "os_id", nullable = false)
	private OS os;

	@Column(name = "version_name", nullable = false)
	private String versionName;

	@Column(name = "version_code", nullable = false)
	private Long versionCode;

	@Column(name = "package_name")
	private String packageName;

	@Column(name = "signature")
	private String signature;

	@Column(name = "app_update_status")
	@Enumerated(EnumType.STRING)
	private AppUpdateStatus appUpdateStatus;

	@Column(name = "app_status")
	@Enumerated(EnumType.STRING)
	private AppStatus appStatus;

	@Column(name = "app_download_count", columnDefinition = "BIGINT default 0", nullable = false)
	private Long appDownloadCount;

	@Column(name = "guide_download_count", columnDefinition = "BIGINT default 0", nullable = false)
	private Long guideDownloadCount;

	@Builder
	App(
		String uuid, String appUrl, String guideUrl, String imageUrl, Device device, Product product, OS os,
		String versionName, Long versionCode,
		String packageName, String signature, AppUpdateStatus appUpdateStatus, AppStatus appStatus
	) {
		this.uuid = uuid;
		this.appUrl = appUrl;
		this.guideUrl = guideUrl;
		this.imageUrl = imageUrl;
		this.device = device;
		this.product = product;
		this.os = os;
		this.versionName = versionName;
		this.versionCode = versionCode;
		this.packageName = packageName;
		this.signature = signature;
		this.appUpdateStatus = appUpdateStatus;
		this.appStatus = appStatus;
		this.appDownloadCount = 0L;
		this.guideDownloadCount = 0L;
	}
}