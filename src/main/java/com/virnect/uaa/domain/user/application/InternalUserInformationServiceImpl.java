package com.virnect.uaa.domain.user.application;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.user.dao.SecessionUserRepository;
import com.virnect.uaa.domain.user.dao.user.UserRepository;
import com.virnect.uaa.domain.user.domain.SecessionUser;
import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.domain.user.dto.response.PageMetadataResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoListResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoResponse;
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
		// 1. 탈퇴 회원 조회
		List<SecessionUser> secessionUsers = secessionUserRepository.findByUserUUIDIn(workspaceUserIdList);

		if (!secessionUsers.isEmpty()) {
			List<String> secessionUserUUIDList = secessionUsers.stream()
				.map(SecessionUser::getUserUUID)
				.collect(Collectors.toList());
			workspaceUserIdList.removeAll(secessionUserUUIDList);
		}

		List<User> userList = userRepository.findByUuidIn(workspaceUserIdList);

		for (SecessionUser secessionUser : secessionUsers) {
			userInfos.add(userInfoMapper.toUserInfoResponse(secessionUser));
		}

		for (User user : userList) {
			userInfos.add(userInfoMapper.toUserInfoResponse(user));
		}

		if (StringUtils.hasText(search)) {
			log.info("[User Info Search][keyword] -> [{}]", search);
			userInfos = userInfos.stream()
				.filter(u -> u.getEmail().contains(search) || u.getNickname().contains(search))
				.collect(Collectors.toList());
		}

		return new UserInfoListResponse(userInfos, null);
	}

	@Override
	public UserInfoResponse getUserInfoByUserId(long userId) {
		return null;
	}
}
