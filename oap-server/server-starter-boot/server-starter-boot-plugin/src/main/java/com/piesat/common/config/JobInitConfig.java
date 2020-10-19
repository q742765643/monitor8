package com.piesat.common.config;

import com.piesat.skywalking.api.alarm.AlarmConfigService;
import com.piesat.skywalking.service.quartz.timing.*;
import com.piesat.skywalking.service.timing.JobScheduleHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class JobInitConfig implements ApplicationRunner {
    @Autowired
    private AutoDiscoveryQuartzService autoDiscoveryQuartzService;
    @Autowired
    private HostConfigQuartzService hostConfigQuartzService;
    @Autowired
    private AlarmConfigQuartzService alarmConfigQuartzService;
    @Autowired
    private FileMonitorQuartzService fileMonitorQuartzService;
    @Autowired
    private JobScheduleHelper jobScheduleHelper;
    @Autowired
    private CommonQuartzService commonQuartzService;
    @Autowired
    private JobInfoQuartzService jobInfoQuartzService;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        autoDiscoveryQuartzService.initJob();
        hostConfigQuartzService.initJob();
        alarmConfigQuartzService.initJob();
        fileMonitorQuartzService.initJob();
        commonQuartzService.initJob();
        jobInfoQuartzService.initJob();
        jobScheduleHelper.start();

    }
}
