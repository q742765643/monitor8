package com.htht;

import com.alibaba.fastjson.JSON;
import com.piesat.skywalking.om.protocol.snmp.SNMPConstants;
import com.piesat.skywalking.om.protocol.snmp.SNMPSessionUtil;
import org.snmp4j.PDU;
import org.snmp4j.util.TableEvent;

import java.util.ArrayList;
import java.util.List;

public class SNMPSessionUtilTest {
    public static void main(String args[]) throws Exception {
        SNMPSessionUtil snmpSessionUtil=new SNMPSessionUtil("10.1.100.8","161","public", "2");
      /*  String[] oid = {".1.3.6.1.4.1.2021.11.9"};
        String[] oids = {SNMPConstants.SSCPUNUM};
        //ArrayList<String> snmpGet = snmpSessionUtil.snmpWalk2( oids);
        String[] sysCpu = {
                SNMPConstants.SSCPURAWIDLE,
                SNMPConstants.SSCPURAWNICE,
                SNMPConstants.SSCPURAWSOFTIRQ,
                SNMPConstants.SSCPURAWSYSTEM,
                SNMPConstants.SSCPURAWWAIT,
                SNMPConstants.SSCPURAWSTEAL,
                ".1.3.6.1.4.1.2021.11.9.1",
        };
        ArrayList<String> snmpGet = snmpSessionUtil.getSnmpGet(PDU.GET, sysCpu);*/
        String[] sysLoad = {
                ".1.3.6.1.4.1.2021.13.15.1.1.2",
                ".1.3.6.1.4.1.2021.13.15.1.1.5",
                ".1.3.6.1.4.1.2021.13.15.1.1.6",
                ".1.3.6.1.4.1.2021.13.15.1.1.12",
                ".1.3.6.1.4.1.2021.13.15.1.1.13"

        };

        List<TableEvent> snmpGet = snmpSessionUtil.snmpWalk( sysLoad);
        System.out.println(JSON.toJSONString(snmpGet));



    }
}
