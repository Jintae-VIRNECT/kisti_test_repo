package com.virnect.serviceserver.global.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;

import feign.Logger;
import feign.Retryer;

@Configuration
@EnableFeignClients(basePackages = "com.virnect.data.application")
public class FeignConfiguration {
	// Feign retry set
	@Bean
	public Retryer retryer() {
		return new Retryer.Default(1000, 2000, 3);
	}

	@Bean
	Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}

	/**
	 * RequestParam 에서 LocalDate, LocalDateTime, LocalTime 을 사용을 할 때 ISO formatter 로 보내기 위한 설정
	 *
	 * @see org.springframework.web.bind.annotation.RequestParam
	 */
	@Bean
	public FeignFormatterRegistrar localDateFeignFormatterRegister() {
		return registry -> {
			DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
			registrar.setUseIsoFormat(true);
			registrar.registerFormatters(registry);
		};
	}
}
