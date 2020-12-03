package com.htht;

import com.piesat.skywalking.om.protocol.snmp.SNMPSessionUtil;
import org.snmp4j.PDU;

import java.util.ArrayList;

/**
 * @ClassName : SNMP
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-14 17:27
 */
public class SNMP {
    public static void main(String[] args) {
        SNMPSessionUtil snmp = new SNMPSessionUtil("10.1.254.202", "161", "public", "2");

        String[] oid = {
                ".1.3.6.1.2.1.1.5", //name
        };
        String[] sw = {".1.3.6.1.2.1.17.4.3"};
        try {
            ArrayList<String> list = snmp.getSnmpGet(PDU.GET, oid);
            ArrayList<String> ss = snmp.snmpWalk2(sw);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

