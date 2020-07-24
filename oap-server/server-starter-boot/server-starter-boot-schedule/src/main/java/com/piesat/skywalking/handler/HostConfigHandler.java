package com.piesat.skywalking.handler;

import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.handler.base.BaseHandler;
import com.piesat.skywalking.schedule.service.snmp.SNMPCiscoService;
import com.piesat.skywalking.schedule.service.snmp.SNMPH3cService;
import com.piesat.skywalking.schedule.service.snmp.SNMPRuijieService;
import com.piesat.skywalking.schedule.service.snmp.SNMPServerService;
import com.piesat.util.ResultT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service("hostConfigHandler")
public class HostConfigHandler implements BaseHandler {
    @Autowired
    private SNMPServerService snmpServerService;
    @Autowired
    private SNMPCiscoService snmpCiscoService;
    @Autowired
    private SNMPRuijieService snmpRuijieService;
    @Autowired
    private SNMPH3cService snmph3cService;
    @Override
    public void execute(JobContext jobContext, ResultT<String> resultT) {
        try {
            long startTime=System.currentTimeMillis();
            HostConfigDto hostConfigDto= (HostConfigDto) jobContext.getHtJobInfoDto();
            String ip= hostConfigDto.getIp();
            String os= hostConfigDto.getOs();
            Date date=new Date(System.currentTimeMillis());
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
