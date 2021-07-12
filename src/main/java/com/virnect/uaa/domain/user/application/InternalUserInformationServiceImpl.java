package com.virnect.uaa.domain.user.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.user.dao.user.UserRepository;
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
	private final UserInfoMapper userInfoMapper;

	@Override
	public UserInfoListResponse findAllUserInfo(
		Pageable pageable
	) {
		Page<User> userPagingResult = userRepository.findAll(pageable);
		List<UserInfoResponse> userInfoResponseList = userPagingResult.stream()
			.map(userInfoMapper::toUSerInfoResponse)
			.collect(Collectors.toList());
		PageMetadataResponse pageMeta = PageMetadataResponse.of(pageable, userPagingResult);
		return new UserInfoListResponse(userInfoResponseList, pageMeta);
	}

	@Override
	public UserInfoListResponse getUsersInfoList(String search, String[] workspaceUserIdList) {
		return null;
	}

	@Override
	public UserInfoResponse getUserInfoByUserId(long userId) {
		return null;
	}
}
