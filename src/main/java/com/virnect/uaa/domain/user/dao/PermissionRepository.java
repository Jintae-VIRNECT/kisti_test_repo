package com.virnect.uaa.domain.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.user.domain.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
	Permission findByPermission(String permissionName);
}
