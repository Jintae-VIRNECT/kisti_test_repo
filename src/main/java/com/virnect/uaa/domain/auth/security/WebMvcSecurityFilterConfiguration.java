package com.virnect.uaa.domain.auth.security;

import java.util.Collections;
import java.util.List;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.virnect.uaa.domain.auth.security.middleware.DeviceAuthenticationResponseEncryptFilter;
import com.virnect.uaa.domain.auth.security.SpringSecurityRestRequestSupportFilter;

@Configuration
public class WebMvcSecurityFilterConfiguration implements WebMvcConfigurer, WebMvcRegistrations {
	private static final List<String> ENCRYPT_API_URLS = Collections.singletonList("/auth/app");

	@Bean
	public FilterRegistrationBean<DeviceAuthenticationResponseEncryptFilter> deviceAuthenticationEncryptFilter() {
		FilterRegistrationBean<DeviceAuthenticationResponseEncryptFilter> encryptFilter = new FilterRegistrationBean<>();
		encryptFilter.setFilter(new DeviceAuthenticationResponseEncryptFilter());
		encryptFilter.setUrlPatterns(ENCRYPT_API_URLS);
		return encryptFilter;
	}

	@Bean
	public FilterRegistrationBean<SpringSecurityRestRequestSupportFilter> springSecurityRestRequestSupportFilterFilterRegistrationBean() {
		FilterRegistrationBean<SpringSecurityRestRequestSupportFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new SpringSecurityRestRequestSupportFilter());
		registrationBean.addUrlPatterns("/v1/auth/signin", "/v1/auth/signout");
		registrationBean.setOrder(-101);
		return registrationBean;
	}
}
