package com.piesat.skywalking.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName : ThreadPoolConfig
 * @Description :
 * @Author : zzj
 * @Date: 2020-09-14 13:24
 */
@Configuration
public class ThreadPoolConfig {
    @Bean
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(200, 200,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(5000), new ThreadFactoryBuilder().setNameFormat("receive-log-%d").build(), new ThreadPoolExecutor.AbortPolicy());
    }
}

