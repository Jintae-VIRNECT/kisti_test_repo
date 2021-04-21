package com.virnect.uaa.domain.auth.dao.device;

import org.springframework.data.repository.CrudRepository;

import com.virnect.uaa.domain.auth.domain.device.DeviceAuth;

public interface DeviceAuthRepository extends CrudRepository<DeviceAuth, String> {
	DeviceAuth findByDeviceAuthKey(String deviceAuthKey);
}
