package com.virnect.data.global.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    //기본 Thread 수
    private static int TASK_CORE_POOL_SIZE = 1;
    //최대 Thread 수
    private static int TASK_MAX_POOL_SIZE = 1;
    //QUEUE 수
    private static int TASK_QUEUE_CAPACITY = 0;
    //Thread Bean Name
    private final String EXECUTOR_BEAN_NAME = "executor";

    @Resource(name="executor")
    private ThreadPoolTaskExecutor executor;

    @Bean(name="executor")
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(TASK_CORE_POOL_SIZE);
        executor.setMaxPoolSize(TASK_MAX_POOL_SIZE);
        executor.setQueueCapacity(TASK_QUEUE_CAPACITY);
        executor.setBeanName(EXECUTOR_BEAN_NAME);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    /*
     * Thread Process도중 에러 발생시
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }

    /*
     * task 생성전에 pool이 찼는지를 체크
     */
    public boolean checkSampleTaskExecute() {
        boolean result = true;
        System.out.println("Execute Task count :::: " + executor.getActiveCount());
        if(executor.getActiveCount() >= (TASK_MAX_POOL_SIZE + TASK_QUEUE_CAPACITY)) {
            result = false;
        }
        return result;
    }
}