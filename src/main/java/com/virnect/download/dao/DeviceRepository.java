package com.virnect.download.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.download.domain.Device;

public interface DeviceRepository extends JpaRepository<Device, Long> {
	Optional<Device> findByName(String deviceName);
}
