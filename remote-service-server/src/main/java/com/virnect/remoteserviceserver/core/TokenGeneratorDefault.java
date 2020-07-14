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

package com.virnect.remoteserviceserver.core;

import com.virnect.java.client.RemoteServiceRole;
import com.virnect.remoteserviceserver.RemoteServiceServerApplication;
import com.virnect.remoteserviceserver.config.RemoteServiceBuildInfo;
import com.virnect.remoteserviceserver.config.RemoteServiceConfig;
import com.virnect.remoteserviceserver.coturn.CoturnCredentialsService;
import com.virnect.remoteserviceserver.coturn.TurnCredentials;
import com.virnect.remoteserviceserver.kurento.core.KurentoTokenOptions;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class TokenGeneratorDefault implements TokenGenerator {

	@Autowired
	private CoturnCredentialsService coturnCredentialsService;

	@Autowired
	protected RemoteServiceConfig remoteServiceConfig;

	@Autowired
	protected RemoteServiceBuildInfo remoteServiceBuildInfo;

	@Override
	public Token generateToken(String sessionId, RemoteServiceRole role, String serverMetadata,
			KurentoTokenOptions kurentoTokenOptions) throws Exception {
		String token = RemoteServiceServerApplication.wsUrl;
		token += "?sessionId=" + sessionId;
		token += "&token=" + IdentifierPrefixes.TOKEN_ID + RandomStringUtils.randomAlphabetic(1).toUpperCase()
				+ RandomStringUtils.randomAlphanumeric(15);
		token += "&role=" + role.name();
		token += "&version=" + remoteServiceBuildInfo.getOpenViduServerVersion();
		TurnCredentials turnCredentials = null;
		if (this.remoteServiceConfig.isTurnadminAvailable()) {
			turnCredentials = coturnCredentialsService.createUser();
			if (turnCredentials != null) {
				token += "&coturnIp=" + remoteServiceConfig.getCoturnIp();
				token += "&turnUsername=" + turnCredentials.getUsername();
				token += "&turnCredential=" + turnCredentials.getCredential();
			}
		}
		return new Token(token, role, serverMetadata, turnCredentials, kurentoTokenOptions);
	}
}
