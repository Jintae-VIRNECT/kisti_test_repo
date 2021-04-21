package com.virnect.security;

import java.io.Serializable;

import org.springframework.security.core.SpringSecurityCoreVersion;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonSerialize
@JsonDeserialize
public class UserAuthenticationDetails implements Serializable {
	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
	private final String remoteAddress;
	private final String sessionId;

	public UserAuthenticationDetails(String remoteAddress, String sessionId) {
		this.remoteAddress = remoteAddress;
		this.sessionId = sessionId;
	}
}
