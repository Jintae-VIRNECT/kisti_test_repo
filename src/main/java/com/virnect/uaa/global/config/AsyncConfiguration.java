package com.virnect.uaa.global.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description Async process configuration
 * @since 2020.04.03
 */

@Configuration
@EnableAsync
public class AsyncConfiguration {

	@Bean(name = "threadPoolTaskExecutor")
	public Executor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(3);
		taskExecutor.setMaxPoolSize(30);
		taskExecutor.setQueueCapacity(10);
		taskExecutor.setThreadNamePrefix("AsyncMailService -");
		taskExecutor.initialize();
		return taskExecutor;
	}
}
