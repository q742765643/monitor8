package com.piesat.skywalking.handler;

import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.dto.AutoDiscoveryDto;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.dto.model.HtJobInfoDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.handler.base.BaseHandler;
import com.piesat.skywalking.handler.base.BaseShardHandler;
import com.piesat.skywalking.om.protocol.snmp.SNMPConstants;
import com.piesat.skywalking.om.protocol.snmp.SNMPSessionUtil;
import com.piesat.skywalking.util.GetRangeIpUtil;
import com.piesat.skywalking.util.Ping;
import com.piesat.util.BaseDto;
import com.piesat.util.ResultT;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.PDU;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("autoDiscoveryHandler")
public class AutoDiscoveryHandler implements BaseShardHandler {
    @GrpcHthtClient
    private HostConfigService hostConfigService;

    @Override
    public void execute(JobContext jobContext, ResultT<String> resultT) {

        String[] host = {".1.3.6.1.2.1.25.1.2.0"};
        String[] sw = {".1.3.6.1.2.1.17.4.3"};
        String[] router = {".1.3.6.1.2.1.4.1.0"};
        try {
            List<String> allIp = (List<String>) jobContext.getLists();
            List<String> ips = Ping.GetPingSuccess(allIp);

            for(String ip:ips){
                HostConfigDto hostConfig=new HostConfigDto();
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
                    hostConfig.setJobCron("0/10 * * * * ?");
                    hostConfig.setId(ip);
                    hostConfig.setTriggerStatus(1);
                    hostConfigService.save(hostConfig);

                }else {
                    hostConfig.setJobCron("0/10 * * * * ?");
                    hostConfig.setId(ip);
                    hostConfig.setTriggerStatus(0);
                    hostConfig.setType("unknownDevice");
                    hostConfigService.save(hostConfig);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<?> sharding(JobContext jobContext, ResultT<String> resultT) {
        AutoDiscoveryDto autoDiscoveryDto= (AutoDiscoveryDto) jobContext.getHtJobInfoDto();
        List<String> ips = null;
        try {
            if(autoDiscoveryDto.getIpRange().indexOf("-")!=-1){
                ips = GetRangeIpUtil.GetIpListWithMask(autoDiscoveryDto.getIpRange());
            }else{
                ips = GetRangeIpUtil.GetIpListWithMask(autoDiscoveryDto.getIpRange(), 24);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ips;
    }
}
