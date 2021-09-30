package com.virnect.data.dao.group;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.data.domain.group.FavoriteGroup;
import com.virnect.data.domain.group.RemoteGroup;

public interface FavoriteGroupRepository extends JpaRepository<FavoriteGroup, Long>, CustomFavoriteGroupRepository {
}