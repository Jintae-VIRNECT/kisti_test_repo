package com.virnect.download.domain;

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
@Table(name = "app")
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

	@JoinColumn(name = "product_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Product product;

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

	@Column(name = "app_origin_url")
	private String appOriginUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "os_id", nullable = false)
	private OS os;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_id", nullable = false)
	private Device device;

	@Column(name = "image_url", nullable = false)
	private String image;

	@Column(name = "app_download_count", columnDefinition = "BIGINT default 0")
	private Long appDownloadCount;

	@Column(name = "guide_download_count", columnDefinition = "BIGINT default 0")
	private Long guideDownloadCount;

	@Builder
	public App(
		String uuid, String versionName, String packageName, String appOriginUrl, Device device, OS os, String appUrl,
		Product product, String image, Long versionCode, String signature
	) {
		this.uuid = uuid;
		this.versionName = versionName;
		this.versionCode = versionCode;
		this.packageName = packageName;
		this.appUpdateStatus = AppUpdateStatus.OPTIONAL;
		this.appStatus = AppStatus.INACTIVE;
		this.product = product;
		this.appUrl = appUrl;
		this.appOriginUrl = appOriginUrl;
		this.os = os;
		this.device = device;
		this.image = image;
		this.appDownloadCount = 0L;
		this.guideDownloadCount = 0L;
		this.signature = signature;
	}
}