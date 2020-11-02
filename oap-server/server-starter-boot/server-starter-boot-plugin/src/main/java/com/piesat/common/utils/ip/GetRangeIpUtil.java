package com.piesat.common.utils.ip;

import java.util.ArrayList;
import java.util.List;

public class GetRangeIpUtil {
    public static List<String> GetIpListWithMask(String destIp, int netmask) {
        ArrayList<String> list = new ArrayList<>();
        String endIP = IpUtils.getEndIP(destIp, netmask).getEndIP();
        String startIP = IpUtils.getEndIP(destIp, netmask).getStartIP();
        String startInt = startIP.substring(startIP.lastIndexOf(".")).replace(".", "");
        String endInt = endIP.substring(endIP.lastIndexOf(".")).replace(".", "");
        String statrIpList = startIP.substring(0, startIP.lastIndexOf("."));
        for (int i = Integer.parseInt(startInt); i <= Integer.parseInt(endInt); i++) {
            String ip = statrIpList + "." + String.valueOf(i);
            list.add(ip);
        }
        return list;
    }

    public static List<String> GetIpListWithMask(String destIp) {
        ArrayList<String> list = new ArrayList<>();
        String statrIpList = destIp.substring(0, destIp.lastIndexOf("."));
        String range = destIp.substring(destIp.lastIndexOf(".") + 1, destIp.length());
        String[] ranges = range.split("-");
        String startInt = ranges[0];
        String endInt = ranges[1];
        for (int i = Integer.parseInt(startInt); i <= Integer.parseInt(endInt); i++) {
            String ip = statrIpList + "." + String.valueOf(i);
            list.add(ip);
        }
        return list;
    }
}
