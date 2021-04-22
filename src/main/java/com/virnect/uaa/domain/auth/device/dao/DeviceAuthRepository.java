package com.virnect.uaa.domain.auth.device.dao;

import org.springframework.data.repository.CrudRepository;

import com.virnect.uaa.domain.auth.device.domain.DeviceAuth;

public interface DeviceAuthRepository extends CrudRepository<DeviceAuth, String> {
	DeviceAuth findByDeviceAuthKey(String deviceAuthKey);
}
