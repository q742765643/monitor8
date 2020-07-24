package com.piesat.common.config;

import com.piesat.skywalking.service.quartz.timing.AutoDiscoveryQuartzService;
import com.piesat.skywalking.service.quartz.timing.HostConfigQuartzService;
import com.piesat.skywalking.service.timing.JobScheduleHelper;
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
    @Autowired
    private JobScheduleHelper jobScheduleHelper;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        autoDiscoveryQuartzService.initJob();
        hostConfigQuartzService.initJob();
        jobScheduleHelper.start();
    }
}
