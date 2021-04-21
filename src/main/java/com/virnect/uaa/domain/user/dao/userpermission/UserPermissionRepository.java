package com.virnect.uaa.domain.user.dao.userpermission;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.uaa.domain.user.domain.UserPermission;

public interface UserPermissionRepository extends JpaRepository<UserPermission, Long>, UserPermissionCustomRepository {
}
