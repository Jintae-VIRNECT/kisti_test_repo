package com.virnect.data.dao.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.virnect.data.domain.group.FavoriteGroupMember;

@Repository
public interface FavoriteGroupMemberRepository extends JpaRepository<FavoriteGroupMember, Long>, CustomFavoriteGroupMemberRepository {
}
