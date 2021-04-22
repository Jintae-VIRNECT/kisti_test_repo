package com.virnect.uaa.domain.auth.device.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.uaa.domain.auth.device.domain.Device;

public interface DeviceRepository extends JpaRepository<Device, Long> {
	Optional<Device> findByUuid(String deviceId);
}
