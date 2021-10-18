package com.virnect.data.dao.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.virnect.data.domain.group.FavoriteGroup;
import com.virnect.data.domain.group.RemoteGroup;

@Repository
public interface FavoriteGroupRepository extends JpaRepository<FavoriteGroup, Long>, CustomFavoriteGroupRepository {
}