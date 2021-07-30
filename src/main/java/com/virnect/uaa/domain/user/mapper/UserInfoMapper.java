package com.virnect.uaa.domain.user.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.virnect.uaa.domain.user.domain.SecessionUser;
import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.domain.user.dto.request.UserInfoModifyRequest;
import com.virnect.uaa.domain.user.dto.response.UserInfoResponse;

@Mapper(
	componentModel = "spring",
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT,
	nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
	nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public abstract class UserInfoMapper {

	@Mapping(source = "user.profile", target = "profile", defaultValue = "dedfault")
	@Mapping(source = "user.nickname", target = "nickname", defaultValue = "")
	@Mapping(source = "user.answer", target = "answer", defaultValue = "")
	@Mapping(source = "user.question", target = "question", defaultValue = "")
	@Mapping(source = "user.recoveryEmail", target = "recoveryEmail", defaultValue = "")
	@Mapping(source = "user.mobile", target = "mobile", defaultValue = "")
	@Mapping(source = "user.description", target = "description", defaultValue = "")
	public abstract UserInfoResponse toUserInfoResponse(User user);

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
	public abstract UserInfoResponse toUserInfoResponse(SecessionUser secessionUser);

	@Mapping(target = "user.id", ignore = true)
	@Mapping(target = "user.uuid", ignore = true)
	@Mapping(target = "user.serviceInfo", ignore = true)
	@Mapping(target = "user.joinInfo", ignore = true)
	@Mapping(target = "user.userType", ignore = true)
	@Mapping(target = "user.accountNonExpired", ignore = true)
	@Mapping(target = "user.accountNonLocked", ignore = true)
	@Mapping(target = "user.credentialsNonExpired", ignore = true)
	@Mapping(target = "user.enabled", ignore = true)
	@Mapping(target = "user.password", ignore = true)
	@Mapping(source = "updateRequest.phoneNumber", target = "user.mobile")
	public abstract void updateFromDetailUpdateRequest(UserInfoModifyRequest updateRequest, @MappingTarget User user);

	@AfterMapping
	protected void userInformationUpdateHandle(UserInfoModifyRequest updateRequest, @MappingTarget User user) {
		if ((updateRequest.getQuestion() != null ||
			updateRequest.getAnswer() != null ||
			updateRequest.getPassword() != null)
			&& !user.isAccountPasswordInitialized()
		) {
			user.setAccountPasswordInitialized(true);
		}
	}
}
