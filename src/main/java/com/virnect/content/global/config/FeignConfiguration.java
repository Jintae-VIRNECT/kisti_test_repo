package com.virnect.content.global.config;

import feign.Logger;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-01-21
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Configuration
@EnableFeignClients(basePackages = {"com.virnect.content.application", "com.virnect.content.domain.process.application"})
public class FeignConfiguration {
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
