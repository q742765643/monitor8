package com.piesat.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PingUtil {
    public static ResultT<String> ping(String ipAddress) {
        BufferedReader in = null;
        ResultT<String> resultT=new ResultT<>();
        StringBuffer result = new StringBuffer();
        Runtime r = Runtime.getRuntime();  // 将要执行的ping命令,此命令是windows格式的命令
        //String pingCommand = "ping " + ipAddress + " -n " + pingTimes    + " -w " + timeOut;
        String pingCommand = "ping " + ipAddress + " -w 4" ;
        try {   // 执行命令并获取输出
            System.out.println(pingCommand);
            Process p = r.exec(pingCommand);
            if (p == null) {
                resultT.setErrorMessage("ping 异常");
                return resultT;
            }
            in = new BufferedReader(new InputStreamReader(p.getInputStream(),"GBK"));   // 逐行检查输出,计算类似出现=23ms TTL=62字样的次数

            String line = null;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }   // 如果出现类似=23ms TTL=62这样的字样,出现的次数=测试次数则返回真
            resultT.setData(result.toString());
            return resultT;
        } catch (Exception ex) {
            resultT.setErrorMessage("ping 异常");
            return resultT;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) throws Exception {
        String ipAddress = "10.1.100.10";
        ResultT<String> data=ping(ipAddress);
        Pattern pattern = Pattern.compile("[\\w\\W]*丢失 = ([0-9]\\d*)[\\w\\W]*");
        Matcher matcher = pattern.matcher(data.getData());
        while (matcher.find()) {
            System.out.println(matcher.group(1));
        }
    }

}
