package com.virnect.content.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "live_share_user")
@NoArgsConstructor
public class LiveShareUser extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "live_share_user_id")
	private Long id;

	@Column(name = "room_id", nullable = false)
	private Long roomId;

	@Column(name = "user_uuid", nullable = false)
	private String userUUID;

	@Column(name = "user_nickname")
	private String userNickname;

	@Column(name = "user_email")
	private String userEmail;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "user_role", nullable = false)
	private Role userRole;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "status", nullable = false)
	private ActiveOrInactive status;

	@Builder(builderClassName = "LiveShareUserBuilder", builderMethodName = "liveShareUserBuilder")
	public LiveShareUser(
		 Long roomId, String userUUID,
		Role userRole
	) {
		this.roomId = roomId;
		this.userUUID = userUUID;
		this.userRole = userRole;
		this.status = ActiveOrInactive.ACTIVE;
	}
}
