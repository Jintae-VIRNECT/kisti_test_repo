package com.virnect.uaa.global.config.session;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ConfigurationProperties(prefix = "cookie.session")
@ConditionalOnProperty(prefix = "spring.session.store-type", havingValue = "redis")
public class SessionCookieProperty {
	private String name = "VSESSION";
	private String path = "/";
	private String domain = "localhost";
	private String sameSite = "Lax";
	private String domainPattern;
	private boolean secureCookieEnabled = true;
	private boolean httpOnlyCookieEnabled = false;
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

	public String getDomainPattern() {
		return domainPattern;
	}

	public void setDomainPattern(String domainPattern) {
		this.domainPattern = domainPattern;
	}

	public boolean isSecureCookieEnabled() {
		return secureCookieEnabled;
	}

	public void setSecureCookieEnabled(boolean secureCookieEnabled) {
		this.secureCookieEnabled = secureCookieEnabled;
	}

	public boolean isHttpOnlyCookieEnabled() {
		return httpOnlyCookieEnabled;
	}

	public void setHttpOnlyCookieEnabled(boolean httpOnlyCookieEnabled) {
		this.httpOnlyCookieEnabled = httpOnlyCookieEnabled;
	}

	boolean hasDomainNamePattern() {
		return StringUtils.hasText(this.domainPattern);
	}

	public String getSameSite() {
		return sameSite;
	}

	public void setSameSite(String sameSite) {
		this.sameSite = sameSite;
	}

	public boolean isDomainEnabled() {
		return domainEnabled;
	}

	public void setDomainEnabled(boolean domainEnabled) {
		this.domainEnabled = domainEnabled;
	}

	@Override
	public String toString() {
		return "SessionCookieProperty{" +
			"name='" + name + '\'' +
			", path='" + path + '\'' +
			", domain='" + domain + '\'' +
			", sameSite='" + sameSite + '\'' +
			", domainPattern='" + domainPattern + '\'' +
			", secureCookieEnabled=" + secureCookieEnabled +
			", httpOnlyCookieEnabled=" + httpOnlyCookieEnabled +
			", domainEnabled=" + domainEnabled +
			'}';
	}
}
