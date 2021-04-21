package com.virnect.uaa;

import org.apache.tomcat.util.http.LegacyCookieProcessor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.global.config.rememberme.RememberMeCookieProperty;
import com.virnect.uaa.global.config.session.SessionCookieProperty;
import com.virnect.uaa.global.config.token.TokenProperty;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties({TokenProperty.class, SessionCookieProperty.class, RememberMeCookieProperty.class})
public class UserAccountAndAuthenticationServer {

	public static void main(String[] args) {
		SpringApplication.run(UserAccountAndAuthenticationServer.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public WebServerFactoryCustomizer<TomcatServletWebServerFactory> cookieProcessorCustomizer() {
		return (factory -> factory.addContextCustomizers(
			(context -> context.setCookieProcessor(new LegacyCookieProcessor()))));
	}
}
