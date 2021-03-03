package com.piesat.skywalking.handler;

import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.handler.base.BaseHandler;
import com.piesat.skywalking.om.protocol.snmp.SNMPConstants;
import com.piesat.skywalking.om.protocol.snmp.SNMPSessionUtil;
import com.piesat.skywalking.schedule.service.snmp.*;
import com.piesat.skywalking.util.IdUtils;
import com.piesat.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.snmp4j.PDU;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service("hostConfigHandler")
public class HostConfigHandler implements BaseHandler {
    @Autowired
    private SNMPWindowsService snmpWindowsService;
    @Autowired
    private SNMPLinuxService snmpLinuxService;
    @Autowired
    private SNMPCiscoService snmpCiscoService;
    @Autowired
    private SNMPRuijieService snmpRuijieService;
    @Autowired
    private SNMPH3cService snmph3cService;
    @Autowired
    private SNMPHuaWeiService snmpHuaWeiService;
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;

    @Override
    public void execute(JobContext jobContext, ResultT<String> resultT) {
        try {

            long startTime = System.currentTimeMillis();
            HostConfigDto hostConfigDto = (HostConfigDto) jobContext.getHtJobInfoDto();
            String ip = hostConfigDto.getIp();
            String os = hostConfigDto.getOs();
            Date date = new Date(System.currentTimeMillis());
            SNMPSessionUtil snmp = new SNMPSessionUtil(ip, "161", "public", "2");
            try {
                if (0==hostConfigDto.getMediaType()) {
                    snmpWindowsService.getSystemInfo(ip, "161", "2", date, snmp);
                }else if (1==hostConfigDto.getMediaType()) {
                    snmpLinuxService.getSystemInfo(ip, "161", "2", date, snmp);
                }else{
                    if(StringUtil.isEmpty(os)){
                        String[] sysDesc = {SNMPConstants.SYSDESC};
                        ArrayList<String> sysDescs = snmp.getSnmpGet(PDU.GET, sysDesc);
                        os=sysDescs.get(0);
                    }
                    if (os.indexOf("Cisco") != -1) {
                        snmpCiscoService.getSystemInfo(ip, "161", "2", date, snmp);
                    } else if (os.indexOf("Ruijie") != -1) {
                        snmpRuijieService.getSystemInfo(ip, "161", "2", date, snmp);
                    } else if (os.indexOf("H3C") != -1) {
                        log.info("H3C交换机采集");
                        snmph3cService.getSystemInfo(ip, "161", "2", date, snmp);
                    }else if (os.indexOf("Huawei") != -1) {
                        log.info("Huawei交换机采集");
                        snmpHuaWeiService.getSystemInfo(ip, "161", "2", date, snmp);
                    }  else {
                        snmpLinuxService.getSystemInfo(ip, "161", "2", date, snmp);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                log.error("ip:{}采集失败,错误信息为:{}", ip, OwnException.get(e));

            } finally {
                snmp.close();
            }
            long endTime = System.currentTimeMillis();
            log.info("{}snmp采集结束,耗时{}s", ip, (endTime - startTime) / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
