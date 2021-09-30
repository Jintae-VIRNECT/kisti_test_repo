package com.virnect.uaa.domain.auth.device.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
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

import com.virnect.uaa.domain.user.domain.BaseTimeEntity;

@Entity
@Getter
@Setter
@Table(name = "device_access_log")
@NoArgsConstructor
public class DeviceAccessLog extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "device_access_log_id")
	private Long deviceAccessLogId;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "user_uuid")
	private String userUUID;

	@Column(name = "user_email")
	private String email;

	@Column(name = "user_register_at")
	private LocalDateTime userRegisteredDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_id")
	private Device device;

	@Builder
	public DeviceAccessLog(
		String userName, String userUUID, String email, Device device, LocalDateTime userRegisteredDate
	) {
		this.userName = userName;
		this.userUUID = userUUID;
		this.email = email;
		this.device = device;
		this.userRegisteredDate = userRegisteredDate;
	}
}
