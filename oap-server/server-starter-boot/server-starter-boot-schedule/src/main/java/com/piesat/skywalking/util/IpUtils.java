package com.piesat.skywalking.util;


import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 获取IP方法
 *
 * @author ruoyi
 */
public class IpUtils {
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    public static boolean internalIp(String ip) {
        byte[] addr = textToNumericFormatV4(ip);
        return internalIp(addr) || "127.0.0.1".equals(ip);
    }

    private static boolean internalIp(byte[] addr) {
        if (StringUtils.isNull(addr) || addr.length < 2) {
            return true;
        }
        final byte b0 = addr[0];
        final byte b1 = addr[1];
        // 10.x.x.x/8
        final byte SECTION_1 = 0x0A;
        // 172.16.x.x/12
        final byte SECTION_2 = (byte) 0xAC;
        final byte SECTION_3 = (byte) 0x10;
        final byte SECTION_4 = (byte) 0x1F;
        // 192.168.x.x/16
        final byte SECTION_5 = (byte) 0xC0;
        final byte SECTION_6 = (byte) 0xA8;
        switch (b0) {
            case SECTION_1:
                return true;
            case SECTION_2:
                if (b1 >= SECTION_3 && b1 <= SECTION_4) {
                    return true;
                }
            case SECTION_5:
                switch (b1) {
                    case SECTION_6:
                        return true;
                }
            default:
                return false;
        }
    }

    /**
     * 将IPv4地址转换成字节
     *
     * @param text IPv4地址
     * @return byte 字节
     */
    public static byte[] textToNumericFormatV4(String text) {
        if (text.length() == 0) {
            return null;
        }

        byte[] bytes = new byte[4];
        String[] elements = text.split("\\.", -1);
        try {
            long l;
            int i;
            switch (elements.length) {
                case 1:
                    l = Long.parseLong(elements[0]);
                    if ((l < 0L) || (l > 4294967295L))
                        return null;
                    bytes[0] = (byte) (int) (l >> 24 & 0xFF);
                    bytes[1] = (byte) (int) ((l & 0xFFFFFF) >> 16 & 0xFF);
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 2:
                    l = Integer.parseInt(elements[0]);
                    if ((l < 0L) || (l > 255L))
                        return null;
                    bytes[0] = (byte) (int) (l & 0xFF);
                    l = Integer.parseInt(elements[1]);
                    if ((l < 0L) || (l > 16777215L))
                        return null;
                    bytes[1] = (byte) (int) (l >> 16 & 0xFF);
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 3:
                    for (i = 0; i < 2; ++i) {
                        l = Integer.parseInt(elements[i]);
                        if ((l < 0L) || (l > 255L))
                            return null;
                        bytes[i] = (byte) (int) (l & 0xFF);
                    }
                    l = Integer.parseInt(elements[2]);
                    if ((l < 0L) || (l > 65535L))
                        return null;
                    bytes[2] = (byte) (int) (l >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 4:
                    for (i = 0; i < 4; ++i) {
                        l = Integer.parseInt(elements[i]);
                        if ((l < 0L) || (l > 255L))
                            return null;
                        bytes[i] = (byte) (int) (l & 0xFF);
                    }
                    break;
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return bytes;
    }

    public static String getHostIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
        }
        return "127.0.0.1";
    }

    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
        }
        return "未知";
    }

    /**
     * 根据起始IP地址和子网掩码计算终止IP
     */
    public static Nets getEndIP(String StartIP, int netmask) {
        return getEndIP(StartIP, getMask(netmask));
    }

    /**
     * 根据起始IP地址和子网掩码计算终止IP
     */
    public static Nets getEndIP(String StartIP, String netmask) {
        Nets nets = new Nets();
        String[] start = Negation(StartIP, netmask).split("\\.");
        nets.setStartIP(start[0] + "." + start[1] + "." + start[2] + "." + (Integer.valueOf(start[3]) + 1));
        nets.setEndIP(TaskOR(Negation(StartIP, netmask), netmask));
        nets.setNetMask(netmask);
        return nets;
    }

    /**
     * 根据掩码位计算掩码
     */
    public static String getMask(int masks) {
        if (masks == 1)
            return "128.0.0.0";
        else if (masks == 2)
            return "192.0.0.0";
        else if (masks == 3)
            return "224.0.0.0";
        else if (masks == 4)
            return "240.0.0.0";
        else if (masks == 5)
            return "248.0.0.0";
        else if (masks == 6)
            return "252.0.0.0";
        else if (masks == 7)
            return "254.0.0.0";
        else if (masks == 8)
            return "255.0.0.0";
        else if (masks == 9)
            return "255.128.0.0";
        else if (masks == 10)
            return "255.192.0.0";
        else if (masks == 11)
            return "255.224.0.0";
        else if (masks == 12)
            return "255.240.0.0";
        else if (masks == 13)
            return "255.248.0.0";
        else if (masks == 14)
            return "255.252.0.0";
        else if (masks == 15)
            return "255.254.0.0";
        else if (masks == 16)
            return "255.255.0.0";
        else if (masks == 17)
            return "255.255.128.0";
        else if (masks == 18)
            return "255.255.192.0";
        else if (masks == 19)
            return "255.255.224.0";
        else if (masks == 20)
            return "255.255.240.0";
        else if (masks == 21)
            return "255.255.248.0";
        else if (masks == 22)
            return "255.255.252.0";
        else if (masks == 23)
            return "255.255.254.0";
        else if (masks == 24)
            return "255.255.255.0";
        else if (masks == 25)
            return "255.255.255.128";
        else if (masks == 26)
            return "255.255.255.192";
        else if (masks == 27)
            return "255.255.255.224";
        else if (masks == 28)
            return "255.255.255.240";
        else if (masks == 29)
            return "255.255.255.248";
        else if (masks == 30)
            return "255.255.255.252";
        else if (masks == 31)
            return "255.255.255.254";
        else if (masks == 32)
            return "255.255.255.255";
        return "";
    }

    /**
     * temp1根据temp2取反
     */
    private static String Negation(String StartIP, String netmask) {
        String[] temp1 = StartIP.trim().split("\\.");
        String[] temp2 = netmask.trim().split("\\.");
        int[] rets = new int[4];
        for (int i = 0; i < 4; i++) {
            rets[i] = Integer.parseInt(temp1[i]) & Integer.parseInt(temp2[i]);
        }
        return rets[0] + "." + rets[1] + "." + rets[2] + "." + rets[3];
    }

    /**
     * temp1根据temp2取或
     */
    private static String TaskOR(String StartIP, String netmask) {
        String[] temp1 = StartIP.trim().split("\\.");
        String[] temp2 = netmask.trim().split("\\.");
        int[] rets = new int[4];
        for (int i = 0; i < 4; i++) {
            rets[i] = 255 - (Integer.parseInt(temp1[i]) ^ Integer.parseInt(temp2[i]));
        }
        return rets[0] + "." + rets[1] + "." + rets[2] + "." + (rets[3] - 1);
    }

    /**
     * 计算子网大小
     */
    public static int getPoolMax(int netmask) {
        if (netmask <= 0 || netmask >= 32) {
            return 0;
        }
        int bits = 32 - netmask;
        return (int) Math.pow(2, bits) - 2;
    }

    /**
     * 转换为掩码位数
     */
    public static int getNetMask(String netmarks) {
        StringBuffer sbf;
        String str;
        int inetmask = 0, count = 0;
        String[] ipList = netmarks.split("\\.");
        for (int n = 0; n < ipList.length; n++) {
            sbf = toBin(Integer.parseInt(ipList[n]));
            str = sbf.reverse().toString();
            count = 0;
            for (int i = 0; i < str.length(); i++) {
                i = str.indexOf('1', i);
                if (i == -1) {
                    break;
                }
                count++;
            }
            inetmask += count;
        }
        return inetmask;
    }

    private static StringBuffer toBin(int x) {
        StringBuffer result = new StringBuffer();
        result.append(x % 2);
        x /= 2;
        while (x > 0) {
            result.append(x % 2);
            x /= 2;
        }
        return result;
    }

}

class Nets {
    private String StartIP;
    private String EndIP;
    private String NetMask;

    public String getStartIP() {
        return StartIP;
    }

    public void setStartIP(String startIP) {
        StartIP = startIP;
    }

    public String getEndIP() {
        return EndIP;
    }

    public void setEndIP(String endIP) {
        EndIP = endIP;
    }

    public String getNetMask() {
        return NetMask;
    }

    public void setNetMask(String netMask) {
        NetMask = netMask;
    }
}
