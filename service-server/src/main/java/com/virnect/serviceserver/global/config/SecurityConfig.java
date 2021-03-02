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

package com.virnect.serviceserver.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	/*@Autowired
	RemoteServiceConfig remoteServiceConfig;*/

	/*@Override
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
		source.registerCorsConfiguration("/**", corsConfiguration.applyPermitDefaultValues());

		http.cors().configurationSource(source);
		http.csrf().disable().authorizeRequests();
		http.authorizeRequests().antMatchers("/**").permitAll();
		http.httpBasic();
		http.formLogin().disable();
	}*/

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Security for API REST
		ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry conf = http.cors().and()
			.csrf().disable().authorizeRequests()
			// /api
			.antMatchers("/api/**").permitAll()
			// /config
			.antMatchers(HttpMethod.GET, "/config/remoteservice-publicurl").permitAll()
			.antMatchers(HttpMethod.GET, "/config/**").permitAll()
			// /cdr
			.antMatchers(HttpMethod.GET, "/cdr/**").permitAll()
			// /accept-certificate
			.antMatchers(HttpMethod.GET, "/accept-certificate").permitAll()
			// Dashboard
			.antMatchers(HttpMethod.GET, "/dashboard/**").permitAll();

		// Security for recording layouts
		conf.antMatchers("/layouts/**").permitAll();

		// Security for recorded video files
		/*if (remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingPublicAccess()) {
			conf = conf.antMatchers("/recordings/**").permitAll();
		} else {
			conf = conf.antMatchers("/recordings/**").authenticated();
		}*/

		conf.and().httpBasic();
	}

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedOrigin(CorsConfiguration.ALL);
		config.addAllowedMethod(CorsConfiguration.ALL);
		config.addAllowedHeader(CorsConfiguration.ALL);
		config.setAllowCredentials(false);
		/*config.setAllowedOrigins(Arrays.asList("*"));
		config.setAllowedHeaders(Arrays.asList("*"));
		config.setAllowedMethods(Arrays.asList("*"));*/
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

	/*@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		*//*auth.inMemoryAuthentication().withUser("remote").password("{noop}" + remoteServiceConfig.getRemoteServiceSecret())
				.roles("ADMIN");*//*
	}*/

}
