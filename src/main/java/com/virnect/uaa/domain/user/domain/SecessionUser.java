package com.virnect.uaa.domain.user.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "secession_user")
@NoArgsConstructor
public class SecessionUser extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "secession_user_id")
	private Long id;

	@Column(name = "user_uuid", nullable = false)
	private String userUUID;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "nickname", nullable = false)
	private String nickName;

	@Lob
	@Column(name = "reason", nullable = false)
	private String reason;

	@Column(name = "joined_at", nullable = false)
	private LocalDateTime joinDate;

	@Column(name = "service_info", nullable = false)
	private String serviceInfo;

	@Column(name = "join_info", nullable = false)
	private String joinInfo;

	@Column(name = "service_day", nullable = false)
	private Long serviceDay;

	@Column(name = "secessioned_at", nullable = false)
	private LocalDateTime secessionDate;

	@Builder
	public SecessionUser(
		String userUUID, String email, String reason, String nickName,
		LocalDateTime joinDate, String joinInfo, String serviceInfo,
		long serviceDay, LocalDateTime secessionDate, long userId
	) {
		this.userUUID = userUUID;
		this.userId = userId;
		this.email = email;
		this.nickName = nickName;
		this.reason = reason;
		this.joinDate = joinDate;
		this.joinInfo = joinInfo;
		this.serviceInfo = serviceInfo;
		this.serviceDay = serviceDay;
		this.secessionDate = secessionDate;
	}

	@Override
	public String toString() {
		return "SecessionUser{" +
			"id=" + id +
			", userUUID='" + userUUID + '\'' +
			", userId=" + userId +
			", email='" + email + '\'' +
			", nickName='" + nickName + '\'' +
			", reason='" + reason + '\'' +
			", joinDate=" + joinDate +
			", serviceInfo='" + serviceInfo + '\'' +
			", joinInfo='" + joinInfo + '\'' +
			", serviceDay=" + serviceDay +
			", secessionDate=" + secessionDate +
			'}';
	}
}
