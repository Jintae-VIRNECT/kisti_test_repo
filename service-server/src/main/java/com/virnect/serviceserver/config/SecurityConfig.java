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

package com.virnect.serviceserver.config;

import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

//@Configuration
@Slf4j
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	/*@Autowired
	RemoteServiceConfig remoteServiceConfig;*/

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// CORS Configuration
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		// CORS Configuration
		corsConfiguration.addAllowedOrigin(CorsConfiguration.ALL);
		corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
		corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
		corsConfiguration.setAllowCredentials(false);

		log.info("CORS ACCESS ALLOW ORIGIN : {}", Arrays.toString(corsConfiguration.getAllowedOrigins().toArray()));
		log.info("CORS ACCESS ALLOW METHOD : {}", Arrays.toString(corsConfiguration.getAllowedMethods().toArray()));
		log.info("CORS ACCESS ALLOW HEADER : {}", Arrays.toString(corsConfiguration.getAllowedMethods().toArray()));
		log.info("CORS ENABLE CREDENTIALS : {}", corsConfiguration.getAllowCredentials().toString().toUpperCase());

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);

		http.cors().configurationSource(source);
		http.csrf().disable().authorizeRequests();
		http.authorizeRequests().antMatchers("/**").permitAll();
		http.httpBasic();
		http.formLogin().disable();
	}

	/*@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Security for API REST
		ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry conf = http.cors().and()
				.csrf().disable().authorizeRequests()
				// /api
				.antMatchers("/api/**").authenticated()
				// /config
				.antMatchers(HttpMethod.GET, "/config/remoteservice-publicurl").permitAll()
				.antMatchers(HttpMethod.GET, "/config/**").authenticated()
				// /cdr
				.antMatchers(HttpMethod.GET, "/cdr/**").authenticated()
				// /accept-certificate
				.antMatchers(HttpMethod.GET, "/accept-certificate").permitAll()
				// Dashboard
				.antMatchers(HttpMethod.GET, "/dashboard/**").authenticated();

		// Security for recording layouts
		conf.antMatchers("/layouts/**").authenticated();

		// Security for recorded video files
		if (remoteServiceConfig.getRemoteServiceRecordingPublicAccess()) {
			conf = conf.antMatchers("/recordings/**").permitAll();
		} else {
			conf = conf.antMatchers("/recordings/**").authenticated();
		}

		conf.and().httpBasic();
	}*/

	/*@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedOrigin(CorsConfiguration.ALL);
		config.addAllowedMethod(CorsConfiguration.ALL);
		config.addAllowedHeader(CorsConfiguration.ALL);
		config.setAllowCredentials(false);
		*//*config.setAllowedOrigins(Arrays.asList("*"));
		config.setAllowedHeaders(Arrays.asList("*"));
		config.setAllowedMethods(Arrays.asList("*"));*//*
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}*/

	/*@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		*//*auth.inMemoryAuthentication().withUser("remote").password("{noop}" + remoteServiceConfig.getRemoteServiceSecret())
				.roles("ADMIN");*//*
		*//*auth.inMemoryAuthentication().withUser("OPENVIDUAPP").password("{noop}" + remoteServiceConfig.getRemoteServiceSecret())
				.roles("ADMIN");*//*
	}*/

}
