package com.virnect.uaa.domain.user.mapper;

import org.mapstruct.Mapper;

import com.virnect.uaa.domain.user.domain.UserAccessLog;
import com.virnect.uaa.domain.user.dto.response.UserAccessDeviceInfoResponse;

@Mapper(componentModel = "spring")
public interface UserAccessDeviceInfoMapper {

	UserAccessDeviceInfoResponse ofUserAccessLog(UserAccessLog userAccessLog);
}
