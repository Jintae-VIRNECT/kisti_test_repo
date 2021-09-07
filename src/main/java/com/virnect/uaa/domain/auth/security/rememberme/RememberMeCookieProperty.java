package com.virnect.uaa.domain.auth.security.rememberme;

import java.time.Duration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ConfigurationProperties(prefix = "cookie.remember-me")
@ConditionalOnProperty(prefix = "spring.session.store-type", havingValue = "redis")
public class RememberMeCookieProperty {
	private String name = "rememberMe";
	private String path = "/";
	private String domain = "localhost";
	private String rememberMeParameter = "remember-me";
	private int validSeconds = (int)Duration.ofDays(14).toMinutes() * 60; // 14 days
	private boolean secureCookieEnabled = true;
	private boolean domainEnabled = true;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getRememberMeParameter() {
		return rememberMeParameter;
	}

	public void setRememberMeParameter(String rememberMeParameter) {
		this.rememberMeParameter = rememberMeParameter;
	}

	public int getValidSeconds() {
		return validSeconds;
	}

	public void setValidSeconds(int validSeconds) {
		this.validSeconds = validSeconds;
	}

	public boolean isSecureCookieEnabled() {
		return secureCookieEnabled;
	}

	public void setSecureCookieEnabled(boolean secureCookieEnabled) {
		this.secureCookieEnabled = secureCookieEnabled;
	}

	public boolean isDomainEnabled() {
		return domainEnabled;
	}

	public void setDomainEnabled(boolean domainEnabled) {
		this.domainEnabled = domainEnabled;
	}

	@Override
	public String toString() {
		return "RememberMeCookieProperty{" +
			"name='" + name + '\'' +
			", path='" + path + '\'' +
			", domain='" + domain + '\'' +
			", rememberMeParameter='" + rememberMeParameter + '\'' +
			", validSeconds=" + validSeconds +
			'}';
	}
}
