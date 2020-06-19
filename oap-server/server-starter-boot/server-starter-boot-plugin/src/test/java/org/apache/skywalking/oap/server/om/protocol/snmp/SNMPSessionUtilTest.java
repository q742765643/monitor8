package org.apache.skywalking.oap.server.om.protocol.snmp;

import com.alibaba.fastjson.JSON;
import com.piesat.skywalking.om.protocol.snmp.SNMPConstants;
import com.piesat.skywalking.om.protocol.snmp.SNMPSessionUtil;
import org.snmp4j.PDU;

import java.util.ArrayList;

public class SNMPSessionUtilTest {
    public static void main(String args[]) throws Exception {
        SNMPSessionUtil snmpSessionUtil=new SNMPSessionUtil("10.1.100.96","161","public", "2");
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
        String[] sysm = {
                SNMPConstants.MEMTOTALSWAP,
                SNMPConstants.MEMAVAILSWAP,
                SNMPConstants.MEMTOTALREAL,
                SNMPConstants.MEMAVAILREAL,
                SNMPConstants.MEMTOTALFREE,
                ".1.3.6.1.2.1.25.2.2.0",
        };
        ArrayList<String> snmpGet = snmpSessionUtil.getSnmpGet(PDU.GET, sysm);
        System.out.println(JSON.toJSONString(snmpGet));



    }
}
