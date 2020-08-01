package com.piesat.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class PingUtil {
    public static StringBuffer ping(String ipAddress, int pingTimes, int timeOut) {
        BufferedReader in = null;
        StringBuffer result = new StringBuffer();
        Runtime r = Runtime.getRuntime();  // 将要执行的ping命令,此命令是windows格式的命令
        String pingCommand = "ping " + ipAddress + " -n " + pingTimes    + " -w " + timeOut;
        try {   // 执行命令并获取输出
            System.out.println(pingCommand);
            Process p = r.exec(pingCommand);
            if (p == null) {
                return result;
            }
            in = new BufferedReader(new InputStreamReader(p.getInputStream(),"GBK"));   // 逐行检查输出,计算类似出现=23ms TTL=62字样的次数

            String line = null;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }   // 如果出现类似=23ms TTL=62这样的字样,出现的次数=测试次数则返回真
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();   // 出现异常则返回假
            return result;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) throws Exception {
        String ipAddress = "10.1.100.999";
    }

}
