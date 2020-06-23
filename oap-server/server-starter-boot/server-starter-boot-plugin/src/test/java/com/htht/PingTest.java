package com.htht;

import com.piesat.common.utils.ip.Ping;

import java.util.List;

public class PingTest {
    public static void main(String args[]) throws Exception {
        List<String> strings = Ping.GetPingSuccess("10.1.100.1-254");
        for (String string : strings) {
            System.out.println(string);
        }
    }
}
