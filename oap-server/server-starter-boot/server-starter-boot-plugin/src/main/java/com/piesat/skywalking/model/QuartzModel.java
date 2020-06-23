package com.piesat.skywalking.model;

import lombok.Data;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.ObjectAlreadyExistsException;

import java.util.Map;

@Data
public class QuartzModel {
    private String jobName;

    private String jobGroup;

    private String cronExpression;

    Class<? extends Job> jobClass;

    Map<String, Object> jobDataMap;
}
