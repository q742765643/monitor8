package com.piesat.skywalking.service.quartz.bean;

import com.piesat.skywalking.service.snmp.*;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Component
@Slf4j
public class HostConfigJob extends QuartzJobBean {
    @Autowired
    private SNMPServerService snmpServerService;
    @Autowired
    private SNMPCiscoService snmpCiscoService;
    @Autowired
    private SNMPRuijieService snmpRuijieService;
    @Autowired
    private SNMPH3cService snmph3cService;
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            long startTime=System.currentTimeMillis();
            String ip= (String) context.getMergedJobDataMap().get("ip");
            String os= (String) context.getMergedJobDataMap().get("os");
            log.info("{}触发执行snmp采集",ip);
            Date date=context.getScheduledFireTime();
            if(os.indexOf("Cisco")!=-1){
                snmpCiscoService.getSystemInfo(ip,"161","2",date);
            }else if(os.indexOf("Ruijie")!=-1){
                snmpRuijieService.getSystemInfo(ip,"161","2",date);
            }else if(os.indexOf("H3C S5130")!=-1){
                snmph3cService.getSystemInfo(ip,"161","2",date);
            }else {
                snmpServerService.getSystemInfo(ip,"161","2",date);
            }
            long endTime=System.currentTimeMillis();
            log.info("{}snmp采集结束,耗时{}s",ip,(endTime-startTime)/1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
