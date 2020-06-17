package com.piesat.skywalking.om.protocol.snmp;

import lombok.SneakyThrows;
import org.snmp4j.PDU;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class SNMPService {

    @SneakyThrows
    public List<Map<String,Object>> getSystemInfo(String hostComputer, String port, String version){
        List<Map<String,Object>> mapList=new ArrayList<>();
        SNMPSessionUtil snmpSessionUtil=new SNMPSessionUtil("10.1.100.96","161","public", "2");
        String[] sysDesc = {SNMPConstants.SYSDESC};
        ArrayList<String> sysDescs=snmpSessionUtil.getSnmpGet(PDU.GET,sysDesc);

        String[] sysUptime = {SNMPConstants.SYSUPTIME};
        ArrayList<String> sysUptimes=snmpSessionUtil.getSnmpGet(PDU.GET,sysUptime);

        String[] sysName = {SNMPConstants.SYSNAME};
        ArrayList<String> sysNames=snmpSessionUtil.getSnmpGet(PDU.GET,sysName);

        String[] sscpuNum = {SNMPConstants.SSCPUNUM};
        ArrayList<String> sscpuNums=snmpSessionUtil.snmpWalk2(sscpuNum);
        String[] sysCpu = {
                           SNMPConstants.SSCPURAWIDLE,
                           SNMPConstants.SSCPURAWNICE,
                           SNMPConstants.SSCPURAWSOFTIRQ,
                           SNMPConstants.SSCPURAWSYSTEM,
                           SNMPConstants.SSCPURAWWAIT,
                           SNMPConstants.SSCPURAWSTEAL,
                           SNMPConstants.SSCPURAWUSER,
                         };

        ArrayList<String> sysCpusPre=snmpSessionUtil.getSnmpGet(PDU.GET,sysCpu);
        Thread.sleep(1000);
        ArrayList<String> sysCpus=snmpSessionUtil.getSnmpGet(PDU.GET,sysCpu);

        Map<String,Object> source=new HashMap<>();
        source.put("@timestamp","");
        source.put("agent.ephemeral_id","");
        source.put("agent.hostname","");
        source.put("agent.id","");
        source.put("agent.name","");
        source.put("agent.type","");
        source.put("agent.version","");
        source.put("cloud.availability_zone","");
        source.put("cloud.instance.id","");
        source.put("cloud.instance.name","");
        source.put("cloud.machine.type","");
        source.put("cloud.provider","");
        source.put("ecs.version","");
        source.put("event.dataset","");
        source.put("event.duration","");
        source.put("event.module","");
        source.put("host.architecture","");
        source.put("host.containerized","");
        source.put("host.hostname","");
        source.put("host.id","");
        source.put("host.ip","");
        source.put("host.mac","");
        source.put("host.name","");
        source.put("host.os.codename","");
        source.put("host.os.family","");
        source.put("host.os.kernel","");
        source.put("host.os.name","");
        source.put("host.os.platform","");
        source.put("host.os.version","");
        source.put("metricset.name","");
        source.put("metricset.period","");
        source.put("service.type","");
        source.put("system.cpu.cores","");
        source.put("system.cpu.idle.norm.pct","");
        source.put("system.cpu.idle.pct","");
        source.put("system.cpu.iowait.norm.pct","");
        source.put("system.cpu.iowait.pct","");
        source.put("system.cpu.irq.norm.pct","");
        source.put("system.cpu.irq.pct","");
        source.put("system.cpu.nice.norm.pct","");
        source.put("system.cpu.nice.pct","");
        source.put("system.cpu.softirq.norm.pct","");
        source.put("system.cpu.softirq.pct","");
        source.put("system.cpu.steal.norm.pct","");
        source.put("system.cpu.steal.pct","");
        source.put("system.cpu.system.norm.pct","");
        source.put("system.cpu.system.pct","");
        source.put("system.cpu.total.norm.pct","");
        source.put("system.cpu.total.pct","");
        source.put("system.cpu.user.norm.pct","");
        source.put("system.cpu.user.pct","");
        /*Map<String,Object> host=new HashMap<>();
        host.put("id","");
        host.put("containerized","");
        host.put("name","");
        host.put("ip",new ArrayList<String>());
        host.put("mac",new ArrayList<String>());
        host.put("hostname","");
        host.put("architecture","");
        Map<String,Object> os=new HashMap<>();
        os.put("name","");
        os.put("kernel","");
        os.put("codename","");
        os.put("platform","");
        os.put("version","");
        os.put("family","");
        host.put("os",os);
        source.put("host",host);

        Map<String,Object> cloud=new HashMap<>();
        Map<String,Object> instance=new HashMap<>();
        instance.put("id","");
        instance.put("name","");
        cloud.put("instance",instance);
        Map<String,Object> machine=new HashMap<>();
        machine.put("type","");
        cloud.put("machine",machine);*/

        return mapList;
    }
}
