package com.virnect.uaa.domain.auth.account.application;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.account.dto.ClientGeoIPInfo;
import com.virnect.uaa.domain.user.dao.useraccesslog.UserAccessLogRepository;
import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.domain.user.domain.UserAccessLog;
import com.virnect.uaa.global.common.ClientUserAgentInformationParser;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAccessLogService {
	private final ClientUserAgentInformationParser clientUserAgentInformationParser;
	private final UserAccessLogRepository userAccessLogRepository;

	public ClientGeoIPInfo saveUserAccessLogInformation(User user, HttpServletRequest request) {
		ClientGeoIPInfo clientGeoIPInfo = clientUserAgentInformationParser.getClientGeoIPInformation(request);
		UserAccessLog deviceMetadata = UserAccessLog.ofUserAndClientGeoIPInfo(user, clientGeoIPInfo);
		userAccessLogRepository.save(deviceMetadata);
		return clientGeoIPInfo;
	}

}
