package com.piesat.skywalking.service.quartz.bean;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-12-19 13:04
 **/
public class HthtJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.err.println("hello world");
    }

}

