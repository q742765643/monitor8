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
    public static void main(String[] args){
        SNMPSessionUtil snmp=new SNMPSessionUtil("10.1.100.69","161","public", "2");

        String[] oid = {
                ".1.3.6.1.2.1.25.1.1.0", //name
        };
        try {
            ArrayList<Long> list=snmp.getSnmpGetV(PDU.GET,oid);
            System.out.println(list.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

