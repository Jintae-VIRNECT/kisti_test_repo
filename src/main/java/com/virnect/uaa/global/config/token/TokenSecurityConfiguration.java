package com.virnect.uaa.global.config.token;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.global.security.RestAuthenticationEntryPoint;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description Security Configuration Class
 * @since 2020.03.10
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class TokenSecurityConfiguration extends WebSecurityConfigurerAdapter {
	private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

	@PostConstruct
	public void init(){
		log.info("Spring Security - Token Based Security Configuration Activate !");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic().disable();
		http.cors().disable();
		http.csrf().disable();
		http.headers().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http.authorizeRequests()
			.antMatchers("/auth/**","/healthcheck","/swagger-ui.html", "/v2/api-docs").permitAll()
			.and()
			.exceptionHandling()
			.authenticationEntryPoint(restAuthenticationEntryPoint);
	}

	@Override // ignore check swagger resource
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**",
			"/swagger-ui.html", "/webjars/**", "/swagger/**"
		);

	}
}
