package com.virnect.data.domain.member;

import javax.persistence.*;

import lombok.*;

import com.virnect.data.domain.BaseTimeEntity;

@Entity
@Getter
@Setter
@Table(name = "remote_group_members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RemoteGroupMember extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "remote_group_member_id", nullable = false)
	private Long id;

	@Column(name = "workspace_id", nullable = false)
	private String workspaceId;

	@Column(name = "uuid", nullable = false)
	private String uuid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "remote_group_id")
	private RemoteGroup remoteGroup;

	@Builder
	public RemoteGroupMember(
		RemoteGroup remoteGroup,
		String workspaceId,
		String uuid
	) {
		this.remoteGroup = remoteGroup;
		this.workspaceId = workspaceId;
		this.uuid = uuid;
	}

}
