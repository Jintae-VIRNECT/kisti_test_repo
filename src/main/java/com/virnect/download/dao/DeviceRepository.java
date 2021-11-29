package com.virnect.download.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.download.domain.Device;

public interface DeviceRepository extends JpaRepository<Device, Long> {
	Optional<Device> findByType(String deviceName);
	Optional<Device> findByTypeAndModel(String deviceName, String deviceModel);
    Optional<Device> findByTypeAndProduct_Name(String deviceName, String productName);
	Optional<Device> findByTypeAndModelAndProduct_Name(String deviceName, String modelName, String productName);
}
