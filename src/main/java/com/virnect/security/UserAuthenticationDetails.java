package com.virnect.security;

import java.io.Serializable;

import org.springframework.security.core.SpringSecurityCoreVersion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = {"remoteAddress","sessionId"})
public class UserAuthenticationDetails implements Serializable {
	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
	private String remoteAddress;
	private String sessionId;

	@JsonCreator
	public UserAuthenticationDetails(
		String remoteAddress,
		String sessionId
	) {
		this.remoteAddress = remoteAddress;
		this.sessionId = sessionId;
	}

	@Override
	public String toString() {
		return "UserAuthenticationDetails{" +
			"remoteAddress='" + remoteAddress + '\'' +
			", sessionId='" + sessionId + '\'' +
			'}';
	}
}
