package com.virnect.uaa.domain.user.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.domain.user.dto.response.UserInfoResponse;

@Mapper(componentModel = "spring")
public interface UserInfoMapper {
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
	UserInfoResponse toUSerInfoResponse(User user);
}
