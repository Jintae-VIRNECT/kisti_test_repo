package com.virnect.data.domain.group;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.data.domain.BaseTimeEntity;

@Entity
@Getter
@Setter
@Table(name = "favorite_group_members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteGroupMember extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "favorite_group_member_id", nullable = false)
	private Long id;

	@Column(name = "uuid", nullable = false)
	private String uuid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "favorite_group_id")
	private FavoriteGroup favoriteGroup;

	@Builder
	public FavoriteGroupMember(
		FavoriteGroup favoriteGroup,
		String uuid
	) {
		this.favoriteGroup = favoriteGroup;
		this.uuid = uuid;
	}

}
