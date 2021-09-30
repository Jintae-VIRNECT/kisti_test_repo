package com.virnect.uaa.domain.auth.device.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.uaa.global.domain.BaseTimeEntity;

@Entity
@Getter
@Setter
@Table(name = "device")
@NoArgsConstructor
public class Device extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "device_id")
	private Long deviceId;

	@Column(name = "uuid", unique = true, nullable = false)
	private String uuid;

	@Column(name = "device_type")
	private String deviceType;

	@Column(name = "app_name")
	private String appName;

	@Column(name = "app_version")
	private String appVersion;

	@Column(name = "model")
	private String model;

	@Column(name = "manufacture")
	private String manufacture;

	@OneToMany(mappedBy = "device", fetch = FetchType.LAZY)
	private List<DeviceAccessLog> deviceAccessLogList = new ArrayList<>();

	@Builder
	public Device(String uuid, String deviceType, String appName, String appVersion, String model, String manufacture) {
		this.uuid = uuid;
		this.deviceType = deviceType;
		this.appName = appName;
		this.appVersion = appVersion;
		this.manufacture = manufacture;
		this.model = model;
	}
}

