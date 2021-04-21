package com.virnect.uaa.domain.auth.dao.device;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.uaa.domain.auth.domain.device.Device;

public interface DeviceRepository extends JpaRepository<Device, Long> {
	Optional<Device> findByUuid(String deviceId);
}
