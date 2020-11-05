package com.piesat.skywalking.handler;

import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.handler.base.BaseHandler;
import com.piesat.skywalking.om.protocol.snmp.SNMPSessionUtil;
import com.piesat.skywalking.schedule.service.snmp.*;
import com.piesat.skywalking.util.IdUtils;
import com.piesat.util.IndexNameUtil;
import com.piesat.util.OwnException;
import com.piesat.util.PingUtil;
import com.piesat.util.ResultT;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
                if (os.indexOf("Cisco") != -1) {
                    snmpCiscoService.getSystemInfo(ip, "161", "2", date, snmp);
                } else if (os.indexOf("Ruijie") != -1) {
                    snmpRuijieService.getSystemInfo(ip, "161", "2", date, snmp);
                } else if (os.indexOf("H3C S5130") != -1) {
                    snmph3cService.getSystemInfo(ip, "161", "2", date, snmp);
                } else if (os.indexOf("Windows") != -1) {
                    snmpWindowsService.getSystemInfo(ip, "161", "2", date, snmp);
                } else {
                    snmpLinuxService.getSystemInfo(ip, "161", "2", date, snmp);
                }
            } catch (Exception e) {
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
