package com.virnect.content.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "live_share_room")
@NoArgsConstructor
public class LiveShareRoom extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "live_share_room_id")
	private Long id;

	@Column(name = "content_uuid", nullable = false)
	private String contentUUID;

	@Column(name = "workspace_uuid", nullable = false)
	private String workspaceUUID;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "status", nullable = false)
	private ActiveOrInactive status;

	@Builder(builderClassName = "LiveShareRoomBuilder", builderMethodName = "liveShareRoomBuilder")
	public LiveShareRoom(
		 String contentUUID, String workspaceUUID
	) {
		this.contentUUID = contentUUID;
		this.workspaceUUID = workspaceUUID;
		this.status = ActiveOrInactive.ACTIVE;
	}

	public void setStatus(ActiveOrInactive status) {
		this.status = status;
	}
}
