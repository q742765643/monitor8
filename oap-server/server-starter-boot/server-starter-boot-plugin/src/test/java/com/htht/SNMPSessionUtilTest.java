package com.htht;

import com.alibaba.fastjson.JSON;
import com.piesat.skywalking.om.protocol.snmp.SNMPSessionUtil;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import java.util.List;

public class SNMPSessionUtilTest {
    public static void main(String args[]) throws Exception {
        SNMPSessionUtil dv = new SNMPSessionUtil("127.0.0.1", "161", "public", "2");
      /*  String[] oid = {".1.3.6.1.4.1.2021.11.9"};
        String[] oids = {SNMPConstants.SSCPUNUM};
        //ArrayList<String> snmpGet = snmpSessionUtil.snmpWalk2( oids);*/
        String[] sysCpu = {
                ".1.3.6.1.2.1.4.20.1.1", ".1.3.6.1.2.1.4.20.1.2", ".1.3.6.1.2.1.4.20.1.3"
        };
        List<TableEvent> snmpGet = dv.snmpWalk(sysCpu);
        for (int i = 0; i < snmpGet.size(); i++) {
            TableEvent tableEvent = snmpGet.get(i);
            VariableBinding[] values = tableEvent.getColumns();
            String ip = values[0].getVariable().toString();
            String index = values[1].getVariable().toString();
            String getaway = values[2].getVariable().toString();
        }
        String[] sysCpu1 = {
                ".1.3.6.1.2.1.2.2.1.1", ".1.3.6.1.2.1.2.2.1.6"
        };
        List<TableEvent> snmpGet1 = dv.snmpWalk(sysCpu1);
        System.out.println(JSON.toJSONString(snmpGet));

    }
}
