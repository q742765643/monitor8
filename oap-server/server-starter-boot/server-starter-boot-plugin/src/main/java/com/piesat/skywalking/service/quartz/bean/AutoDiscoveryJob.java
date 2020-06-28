package com.piesat.skywalking.service.quartz.bean;

import com.piesat.common.utils.ip.Ping;
import com.piesat.skywalking.entity.HostConfigEntity;
import com.piesat.skywalking.om.protocol.snmp.SNMPConstants;
import com.piesat.skywalking.om.protocol.snmp.SNMPSessionUtil;
import com.piesat.skywalking.service.host.HostConfigService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.snmp4j.PDU;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Component
@Slf4j
public class AutoDiscoveryJob extends QuartzJobBean {
    @Autowired
    private HostConfigService hostConfigService;
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        String ipRange= (String) context.getMergedJobDataMap().get("ipRange");
        log.info("{}触发执行自动发现",ipRange);
        String[] host = {".1.3.6.1.2.1.25.1.2.0"};
        String[] sw = {".1.3.6.1.2.1.17.4.3"};
        String[] router = {".1.3.6.1.2.1.4.1.0"};
        try {
            List<String> ips = Ping.GetPingSuccess(ipRange);
            for(String ip:ips){
                HostConfigEntity hostConfig=new HostConfigEntity();
                hostConfig.setIp(ip);
                SNMPSessionUtil dv = new SNMPSessionUtil(ip,"161", "public", "2");
                if (!"-1".equals(dv.getIsSnmpGet(PDU.GET,".1.3.6.1.2.1.1.5").get(0))) {
                    if (!"noSuchObject".equals(dv.getSnmpGet(PDU.GET,host).get(0))){//服务器
                        hostConfig.setType("server");
                    }else if ("1".equals(dv.getSnmpGet(PDU.GET,router).get(0)) && dv.snmpWalk2(sw).isEmpty()){//路由器
                        hostConfig.setType("router");
                    }else if ("2".equals(dv.getSnmpGet(PDU.GET,router).get(0)) && !dv.snmpWalk2(sw).isEmpty()) {//二层交换机
                        hostConfig.setType("linkSwitch");
                    }else if ("1".equals(dv.getSnmpGet(PDU.GET,router).get(0)) && !dv.snmpWalk2(sw).isEmpty()){//三层交换机
                        hostConfig.setType("networkSwitch");
                    }else {//未知设备
                        hostConfig.setType("unknownDevice");
                    }
                    String[] sysDesc = {SNMPConstants.SYSDESC};
                    ArrayList<String> sysDescs=dv.getSnmpGet(PDU.GET,sysDesc);
                    String[] sysName = {SNMPConstants.SYSNAME};
                    ArrayList<String> sysNames=dv.getSnmpGet(PDU.GET,sysName);
                    hostConfig.setHostName(sysNames.get(0));
                    hostConfig.setOs(sysDescs.get(0));
                    hostConfig.setIsSnmp("1");
                    hostConfig.setCron("0/10 * * * * ?");
                    hostConfig.setId(ip);
                    hostConfig.setStatus("1");
                    hostConfigService.save(hostConfig);

                }else {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
