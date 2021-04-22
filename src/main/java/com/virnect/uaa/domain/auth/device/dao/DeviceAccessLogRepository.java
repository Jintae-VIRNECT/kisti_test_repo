package com.virnect.uaa.domain.auth.device.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.uaa.domain.auth.device.domain.DeviceAccessLog;

public interface DeviceAccessLogRepository extends JpaRepository<DeviceAccessLog, Long> {
}
