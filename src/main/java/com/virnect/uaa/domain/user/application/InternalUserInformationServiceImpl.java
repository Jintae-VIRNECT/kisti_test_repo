package com.virnect.uaa.domain.user.application;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.user.dao.secession.SecessionUserRepository;
import com.virnect.uaa.domain.user.dao.user.UserRepository;
import com.virnect.uaa.domain.user.domain.SecessionUser;
import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.domain.user.dto.response.PageMetadataResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoListResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoResponse;
import com.virnect.uaa.domain.user.error.UserAccountErrorCode;
import com.virnect.uaa.domain.user.exception.UserServiceException;
import com.virnect.uaa.domain.user.mapper.UserInfoMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternalUserInformationServiceImpl implements InternalUserInformationService {
	private final UserRepository userRepository;
	private final SecessionUserRepository secessionUserRepository;
	private final UserInfoMapper userInfoMapper;

	@Override
	public UserInfoListResponse findAllUserInfo(
		Pageable pageable
	) {
		Page<User> userPagingResult = userRepository.findAll(pageable);
		List<UserInfoResponse> userInfoResponseList = userPagingResult.stream()
			.map(userInfoMapper::toUserInfoResponse)
			.collect(Collectors.toList());
		PageMetadataResponse pageMeta = PageMetadataResponse.of(pageable, userPagingResult);
		return new UserInfoListResponse(userInfoResponseList, pageMeta);
	}

	@Override
	public UserInfoListResponse getUsersInfoList(String search, List<String> workspaceUserIdList) {
		List<UserInfoResponse> userInfos = new ArrayList<>();
		addNormalStateUserInformation(userInfos, workspaceUserIdList, search);
		addSecessionUserInformation(userInfos, workspaceUserIdList, search);
		return new UserInfoListResponse(userInfos, null);
	}

	@Override
	public UserInfoResponse getUserInfoByUserId(long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND));
		return userInfoMapper.toUserInfoResponse(user);
	}

	private void addNormalStateUserInformation(
		List<UserInfoResponse> userInfos, List<String> workspaceUserIdList, String search
	) {
		List<User> users = userRepository.findUserInformationInUUIDListWithSearchQuery(workspaceUserIdList, search);
		List<UserInfoResponse> userInfoResponseList = users.stream()
			.map(userInfoMapper::toUserInfoResponse)
			.collect(Collectors.toList());
		userInfos.addAll(userInfoResponseList);
	}

	private void addSecessionUserInformation(
		List<UserInfoResponse> userInfos, List<String> workspaceUserIdList, String search
	) {
		// if, already fetch all workspace user info then didn't querying secession user information
		if (userInfos.size() == workspaceUserIdList.size()) {
			return;
		}

		// remove normal user uuid list in secession user search uuid list
		List<String> removeUUIDList = userInfos.stream().map(UserInfoResponse::getUuid).collect(Collectors.toList());
		workspaceUserIdList.removeAll(removeUUIDList);
		List<SecessionUser> secessionUsers = secessionUserRepository.findSecessionUserInformationByUUIDListWithSearchQuery(
			workspaceUserIdList, search
		);
		userInfos.addAll(secessionUsers.stream().map(userInfoMapper::toUserInfoResponse).collect(Collectors.toList()));
	}
}
