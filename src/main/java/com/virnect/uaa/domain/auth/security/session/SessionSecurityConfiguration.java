package com.virnect.uaa.domain.auth.security.session;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.savedrequest.RequestCacheAwareFilter;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.security.rememberme.CustomRememberMeAuthenticationFilter;
import com.virnect.uaa.domain.auth.security.rememberme.RememberMeCookieProperty;
import com.virnect.uaa.global.security.CommonAuthenticationDetailsSource;
import com.virnect.uaa.global.security.user.LoginFailureHandler;
import com.virnect.uaa.global.security.user.LoginSuccessHandler;
import com.virnect.uaa.global.security.user.LogoutSuccessHandler;
import com.virnect.uaa.global.security.user.UserDetailsServiceImpl;

@Slf4j
@Order(1)
@Configuration
@RequiredArgsConstructor
public class SessionSecurityConfiguration<S extends Session> extends WebSecurityConfigurerAdapter {
	private final static String LOGIN_URL = "/v1/auth/signin";
	private final static String LOGOUT_URL = "/v1/auth/signout";
	private final static String LOGIN_USERNAME_PARAMETER = "email";
	private final static String LOGIN_PASSWORD_PARAMETER = "password";

	private final SpringSessionBackedSessionRegistry<S> sessionRegistry;
	private final CompositeSessionAuthenticationStrategy compositeSessionAuthenticationStrategy;
	private final UserDetailsServiceImpl userDetailsService;
	private final PasswordEncoder passwordEncoder;
	private final LoginSuccessHandler loginSuccessHandler;
	private final LoginFailureHandler loginFailureHandler;
	private final LogoutSuccessHandler logoutSuccessHandler;
	private final SessionCookieProperty sessionCookieProperty;
	private final RememberMeCookieProperty rememberMeCookieProperty;
	private final PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices;

	@PostConstruct
	public void init() {
		log.info("Spring Security - Session Based Security Configuration Activate !");
	}

	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
		daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
		return daoAuthenticationProvider;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(daoAuthenticationProvider());
		auth.authenticationProvider(rememberMeAuthenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// CSRF Security Configuration
		http.csrf().disable();
		// HTTP Basic Authentication Configuration
		http.httpBasic().disable();
		// HTTP CORS Configuration
		http.cors().disable();

		// Login Security Configuration
		http.formLogin()
			.authenticationDetailsSource(commonAuthenticationDetailsSource())
			.loginProcessingUrl(LOGIN_URL)
			.usernameParameter(LOGIN_USERNAME_PARAMETER)
			.passwordParameter(LOGIN_PASSWORD_PARAMETER)
			.successHandler(loginSuccessHandler)
			.failureHandler(loginFailureHandler)
			.permitAll();

		// Logout Security Configuration
		http.logout()
			.logoutUrl(LOGOUT_URL)
			.deleteCookies(sessionCookieProperty.getName(), rememberMeCookieProperty.getName())
			.invalidateHttpSession(true)
			.clearAuthentication(true)
			.logoutSuccessHandler(logoutSuccessHandler);

		// Http Request Authentication Matcher Configuration
		http.authorizeRequests()
			.antMatchers("/v1/**")
			.permitAll()
			.anyRequest().permitAll();

		// Session Management Security Configuration
		http.sessionManagement()
			.sessionAuthenticationStrategy(compositeSessionAuthenticationStrategy);
		http.addFilterBefore(new ConcurrentSessionFilter(sessionRegistry), RequestCacheAwareFilter.class);

		// Remember Me Security Configuration
		http.addFilterBefore(customRememberMeAuthenticationFilter(), RememberMeAuthenticationFilter.class)
			.setSharedObject(RememberMeServices.class, persistentTokenBasedRememberMeServices);
		// Exception Handling
		http.exceptionHandling()
			.accessDeniedHandler((request, response, accessDeniedException) -> {
				response.getWriter().write("access denied.");
			})
			.authenticationEntryPoint(loginFailureHandler::onAuthenticationFailure);
	}

	@Override // ignore check swagger resource
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**",
			"/swagger-ui.html", "/webjars/**", "/swagger/**"
		);
	}

	@Bean
	public CustomRememberMeAuthenticationFilter customRememberMeAuthenticationFilter() throws Exception {
		CustomRememberMeAuthenticationFilter customRememberMeAuthenticationFilter = new CustomRememberMeAuthenticationFilter(
			this.authenticationManagerBean(),
			persistentTokenBasedRememberMeServices, compositeSessionAuthenticationStrategy
		);
		customRememberMeAuthenticationFilter.setSuccessHandler(loginSuccessHandler);
		customRememberMeAuthenticationFilter.setFailureHandler(loginFailureHandler);
		return customRememberMeAuthenticationFilter;
	}

	@Bean
	public RememberMeAuthenticationProvider rememberMeAuthenticationProvider() {
		return new RememberMeAuthenticationProvider(rememberMeCookieProperty.getName());
	}

	@Bean
	CommonAuthenticationDetailsSource commonAuthenticationDetailsSource() {
		return new CommonAuthenticationDetailsSource();
	}
}
