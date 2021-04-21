package com.virnect.uaa.domain.auth.dao.device;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.uaa.domain.auth.domain.device.DeviceAccessLog;

public interface DeviceAccessLogRepository extends JpaRepository<DeviceAccessLog, Long> {
}
