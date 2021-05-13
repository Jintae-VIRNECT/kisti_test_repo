package com.virnect.uaa.global.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.infra.rest.user.dto.UserInfoResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {
	UserMapper USER_MAPPER = Mappers.getMapper(UserMapper.class);

	UserInfoResponse toUserInfoResponse(User user);
}
