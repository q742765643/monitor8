package com.piesat.sso.client.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-12-17 14:00
 **/
@Configuration
public class ThreadPoolConfig {
    @Bean
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(20, 20,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(5000), new ThreadFactoryBuilder().setNameFormat("insert-oper-log-%d").build(), new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean
    public ExecutorService executorTriggerService() {
        return new ThreadPoolExecutor(200, 800,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(5000), new ThreadFactoryBuilder().setNameFormat("trigger-log-%d").build(), new ThreadPoolExecutor.AbortPolicy());
    }
}

