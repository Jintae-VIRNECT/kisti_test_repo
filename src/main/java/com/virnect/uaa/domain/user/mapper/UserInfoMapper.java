package com.virnect.uaa.domain.user.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.virnect.uaa.domain.user.domain.SecessionUser;
import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.domain.user.dto.response.UserInfoResponse;

@Mapper(componentModel = "spring")
public interface UserInfoMapper {
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
	UserInfoResponse toUserInfoResponse(User user);

	@Mapping(source = "secessionUser.nickName", target = "nickname")
	@Mapping(source = "secessionUser.userUUID", target = "uuid")
	@Mapping(target = "name", constant = "secessionUser")
	@Mapping(target = "firstName", constant = "secessionUser")
	@Mapping(target = "description", constant = "secession User Info")
	@Mapping(target = "profile", constant = "default")
	@Mapping(target = "userType", constant = "SECESSION_USER")
	@Mapping(target = "marketInfoReceive", constant = "REJECT")
	@Mapping(target = "accountNonExpired", constant = "true")
	@Mapping(target = "accountNonLocked", constant = "true")
	@Mapping(target = "credentialsNonExpired", constant = "true")
	@Mapping(target = "enabled", constant = "true")
	UserInfoResponse toUserInfoResponse(SecessionUser secessionUser);
}
