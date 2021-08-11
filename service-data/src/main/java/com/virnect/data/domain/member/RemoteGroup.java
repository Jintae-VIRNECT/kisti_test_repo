package com.virnect.data.domain.member;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import lombok.AccessLevel;
import lombok.*;

import com.virnect.data.domain.BaseTimeEntity;

@Entity
@Getter
@Setter
@Table(name = "remote_groups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RemoteGroup extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "remote_group_id", nullable = false)
	private Long id;

	@Column(name = "workspace_id", nullable = false)
	private String workspaceId;

	@Column(name = "group_id", nullable = false)
	private String groupId;

	@Column(name = "group_name", nullable = false)
	private String groupName;

	@Column(name = "uuid", nullable = false)
	private String uuid;

	@OneToMany(mappedBy = "remoteGroup", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private List<RemoteGroupMember> groupMembers = new ArrayList<>();

	@Builder
	public RemoteGroup(
		String workspaceId,
		String groupId,
		String groupName,
		String uuid
	) {
		this.workspaceId = workspaceId;
		this.groupId = groupId;
		this.groupName = groupName;
		this.uuid = uuid;
	}

	@Override
	public String toString() {
		return "RemoteGroup{" +
			"id=" + id +
			", workspaceId='" + workspaceId + '\'' +
			", groupId='" + groupId + '\'' +
			", groupName='" + groupName + '\'' +
			", uuid='" + uuid + '\'' +
			", groupMembers=" + groupMembers +
			'}';
	}
}
