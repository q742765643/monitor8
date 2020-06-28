package com.piesat.common.config;

import com.piesat.skywalking.service.quartz.timing.AutoDiscoveryQuartzService;
import com.piesat.skywalking.service.quartz.timing.HostConfigQuartzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class JobInitConfig implements ApplicationRunner {
    @Autowired
    private AutoDiscoveryQuartzService autoDiscoveryQuartzService;
    @Autowired
    private HostConfigQuartzService hostConfigQuartzService;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        autoDiscoveryQuartzService.initJob();
        hostConfigQuartzService.initJob();
    }
}
