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
        SNMPSessionUtil dv = new SNMPSessionUtil("10.1.6.88", "161", "public", "2");
      /*  String[] oid = {".1.3.6.1.4.1.2021.11.9"};
        String[] oids = {SNMPConstants.SSCPUNUM};
        //ArrayList<String> snmpGet = snmpSessionUtil.snmpWalk2( oids);*/
        String[] sysCpu = {
                ".1.3.6.1.2.1.25.3.3.1.2",
        };
        ArrayList<String> snmpGet = dv.getSnmpGet(PDU.GET, sysCpu);

        System.out.println(JSON.toJSONString(snmpGet));

    }
}
