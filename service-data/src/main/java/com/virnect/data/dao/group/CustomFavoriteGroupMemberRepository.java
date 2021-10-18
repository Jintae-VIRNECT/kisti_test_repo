package com.virnect.data.dao.group;

import java.util.Optional;

import com.virnect.data.domain.group.FavoriteGroupMember;

public interface CustomFavoriteGroupMemberRepository {

	Optional<FavoriteGroupMember> findByFavoriteGroupIdAndUuid(Long favoriteGroupId, String uuid);

	void deleteByFavoriteGroupIdAndUuid(Long favoriteGroupId, String uuid);

}
