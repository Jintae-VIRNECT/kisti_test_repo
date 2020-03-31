package com.virnect.login.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Login
 * @email practice1356@gmail.com
 * @description Web MVC Configuration Class
 * @since 2020.03.31
 */

@Configuration
public class RequestLoggingFilterConfiguration {

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludeHeaders(true);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setMaxPayloadLength(1024 * 1024);
        loggingFilter.setAfterMessagePrefix("REQUEST DATA: ");
        return loggingFilter;
    }
}
