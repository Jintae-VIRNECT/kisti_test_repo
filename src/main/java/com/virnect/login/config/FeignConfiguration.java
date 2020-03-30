package com.virnect.login.config;

import feign.Logger;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;

/**
 * @project: PF-Login
 * @author: jeonghyeon.chang (johnmark)
 * @email: practice1356@gmail.com
 * @since: 2020.03.09
 * @description: Configuration Class for Feign Clients
 */

@Configuration
@EnableFeignClients(basePackages = "com.virnect.login.service")
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
