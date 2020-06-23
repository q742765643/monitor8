package com.piesat.skywalking.service.quartz.bean;

import com.piesat.common.utils.ip.Ping;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.List;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Component
public class AutoDiscoveryJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
       String ipRange= (String) context.getMergedJobDataMap().get("ipRange");
        try {
            List<String> strings = Ping.GetPingSuccess(ipRange);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
