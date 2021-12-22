package com.virnect.download.global.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;

import feign.Retryer;
import lombok.RequiredArgsConstructor;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-17
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Configuration
@EnableFeignClients(basePackages = "com.virnect.download.application")
@RequiredArgsConstructor
public class FeignConfiguration {
	@Bean
	public Retryer retryer() {
		return new Retryer.Default(1000, 2000, 3);
	}

	@Bean
	public FeignFormatterRegistrar localDateFeignFormatterRegister() {
		return registry -> {
			DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
			registrar.setUseIsoFormat(true);
			registrar.registerFormatters(registry);
		};
	}
}
