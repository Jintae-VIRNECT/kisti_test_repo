/*
 * (C) Copyright 2017-2020 OpenVidu (https://openvidu.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.virnect.serviceserver.core;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.virnect.java.client.RemoteServiceRole;
import com.virnect.serviceserver.ServiceServerApplication;
import com.virnect.serviceserver.config.RemoteServiceBuildInfo;
import com.virnect.serviceserver.config.RemoteServiceConfig;
import com.virnect.serviceserver.coturn.CoturnCredentialsService;
import com.virnect.serviceserver.coturn.TurnCredentials;
import com.virnect.serviceserver.kurento.core.KurentoTokenOptions;

public class TokenGeneratorDefault implements TokenGenerator {

	@Autowired
	private CoturnCredentialsService coturnCredentialsService;

	@Autowired
	protected RemoteServiceConfig openviduConfig;

	@Autowired
	protected RemoteServiceBuildInfo openviduBuildConfig;

	@Override
	public Token generateToken(String sessionId, RemoteServiceRole role, String serverMetadata,
			KurentoTokenOptions kurentoTokenOptions) throws Exception {
		String token = ServiceServerApplication.wsUrl;
		token += "?sessionId=" + sessionId;
		token += "&token=" + IdentifierPrefixes.TOKEN_ID + RandomStringUtils.randomAlphabetic(1).toUpperCase()
				+ RandomStringUtils.randomAlphanumeric(15);
		token += "&role=" + role.name();
		token += "&version=" + openviduBuildConfig.getOpenViduServerVersion();
		TurnCredentials turnCredentials = null;
		if (this.openviduConfig.isTurnadminAvailable()) {
			turnCredentials = coturnCredentialsService.createUser();
			if (turnCredentials != null) {
				token += "&coturnIp=" + openviduConfig.getCoturnIp();
				token += "&turnUsername=" + turnCredentials.getUsername();
				token += "&turnCredential=" + turnCredentials.getCredential();
			}
		}
		return new Token(token, role, serverMetadata, turnCredentials, kurentoTokenOptions);
	}
}
